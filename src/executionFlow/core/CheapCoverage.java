package executionFlow.core;

import static java.lang.constant.ConstantDescs.CD_CallSite;
import static java.lang.constant.ConstantDescs.CD_MethodHandles_Lookup;
import static java.lang.constant.ConstantDescs.CD_MethodType;
import static java.lang.constant.ConstantDescs.CD_String;
import static java.lang.constant.ConstantDescs.CD_int;
import static java.lang.invoke.MethodHandles.lookup;
import static org.objectweb.asm.Opcodes.ASM7;
import static org.objectweb.asm.Opcodes.H_INVOKESTATIC;

import java.io.File;
import java.io.IOException;
import java.lang.constant.MethodTypeDesc;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import executionFlow.info.ClassConstructorInfo;
import executionFlow.info.ClassMethodInfo;


/**
 * Helper class that will trace a method execution.
 * 
 * Modified from {@link https://github.com/forax/cheapcoverage}
 */
public class CheapCoverage 
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private static final Handle BSM = new Handle(H_INVOKESTATIC, RT.class.getName().replace('.', '/'), "bsm",
			MethodTypeDesc.of(CD_CallSite, CD_MethodHandles_Lookup, CD_String, CD_MethodType, CD_String, CD_int)
					.descriptorString(),
			false);
	private static Class<?> parsedClass;
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	public static Class<?> getParsedClass() { return parsedClass; }
	
	
	public static void loadClass(String classPath) throws IOException 
	{
		if (classPath == null) 
			throw new IllegalArgumentException("Class path cannot be null");
		
		byte[] data;
		try (var input = Files.newInputStream(Path.of(classPath))) {
			data = input.readAllBytes();
		}
		
		var reader = new ClassReader(data);
		var writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES);
		
		reader.accept(new ClassVisitor(ASM7, writer) {
			@Override
			public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
					String[] exceptions) 
			{	
				var mv = super.visitMethod(access, name, descriptor, signature, exceptions);
				
				return new MethodVisitor(ASM7, mv) {
					@Override
					public void visitLineNumber(int line, Label start) {
						super.visitInvokeDynamicInsn("probe", "()V", BSM, name + descriptor, line);
						super.visitLineNumber(line, start);
					}
				};
			}
			
			
		}, 0);

		var bytecode = writer.toByteArray();
		parsedClass = new ClassLoader() {
			private Class<?> define() {
				return defineClass(null, bytecode, 0, bytecode.length);
			}
		}.define();
	}
	 
	
	public static List<Integer> getTestPath(ClassMethodInfo methodInfo, ClassConstructorInfo constructorInfo) throws Throwable
	{
		RT.clearExecutionPath();
		MethodType methodTypes = methodInfo.getMethodTypes();
		
		try {						// Try to invoke the method as static
			MethodHandle mh = lookup().findStatic(parsedClass, methodInfo.getMethodName(), methodTypes);
			mh.invokeWithArguments(methodInfo.getArgs());
		} catch(Throwable t) {		// Try to invoke the method as non static
			MethodHandle mh = lookup().findVirtual(parsedClass, methodInfo.getMethodName(), methodTypes);
			
			// Checks if it is default constructor (empty)
			if ( constructorInfo == null || 
				 constructorInfo.getConstructorTypes() == null || 
				 constructorInfo.getConstructorArgs().length == 0 ) {
				mh = mh.bindTo(parsedClass.getConstructor().newInstance());
			} else {
				mh = mh.bindTo(parsedClass.getConstructor(constructorInfo.getConstructorTypes())
										  .newInstance(constructorInfo.getConstructorArgs()));
			}
			
			mh.invokeWithArguments(methodInfo.getArgs());
		}
		
		// Remove last line if method has no return (otherwise it considers empty return as a line)
		List<Integer> path = RT.getExecutionPath();
		
		if (methodTypes.returnType() == void.class)
			path.remove(path.size()-1);
		
		return path;
	}
}
