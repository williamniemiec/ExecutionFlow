package executionFlow.cheapCoverage;

import static java.lang.constant.ConstantDescs.CD_CallSite;
import static java.lang.constant.ConstantDescs.CD_MethodHandles_Lookup;
import static java.lang.constant.ConstantDescs.CD_MethodType;
import static java.lang.constant.ConstantDescs.CD_String;
import static java.lang.constant.ConstantDescs.CD_int;
import static java.lang.invoke.MethodHandles.lookup;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.Opcodes.ASM7;
import static org.objectweb.asm.Opcodes.H_INVOKESTATIC;

import java.io.IOException;
import java.lang.constant.MethodTypeDesc;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import executionFlow.info.ClassConstructorInfo;


/**
 * Helper class that will trace a method execution
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
	
	
	public static void parseClass(String classPath) throws IOException 
	{
		byte[] data;
		
		try (var input = Files.newInputStream(Path.of(classPath))) {
			data = input.readAllBytes();
		}
		
		var reader = new ClassReader(data);
		var writer = new ClassWriter(reader, COMPUTE_FRAMES);
		
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
	
	
	public static List<Integer> getExecutionPath(String methodName, MethodType methodTypes, Object[] args, ClassConstructorInfo cci) throws Throwable 
	{
		RT.clearExecutionPath();
		
		try {						// Try to invoke the method as static
			MethodHandle mh = lookup().findStatic(parsedClass, methodName, methodTypes);
			mh.invokeWithArguments(args);
		} catch(Throwable t) {		// Try to invoke the method as non static
			MethodHandle mh = lookup().findVirtual(parsedClass, methodName, methodTypes);
			
			if (cci == null)		// Constructor default (empty)
				mh = mh.bindTo(parsedClass.getConstructor().newInstance());
			else
				mh = mh.bindTo(parsedClass.getConstructor(cci.getConstructorTypes()).newInstance(cci.getConstructorArgs()));
			
			mh.invokeWithArguments(args);
		}
		
		// Remove last line if method has no return (otherwise it considers empty return line)
		List<Integer> path = RT.getExecutionPath();
		
		if (methodTypes.returnType() == void.class)
			path.remove(path.size()-1);
		
		return path;
	}
}
