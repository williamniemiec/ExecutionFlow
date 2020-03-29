package runtime;
import info.ClassMethodInfo;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import executionFlow.CollectorExecutionFlow;

public aspect asp {
	private static Map<String, ClassMethodInfo> collector = new LinkedHashMap<>();
	private static String classPath;
	
	//pointcut pc2(): cflow(execution(* *.*(*))) && cflowbelow(execution(* *.main(*))) &&  !within(asp);
	pointcut pc3(): execution(@Test * *.*(*));
	before(): pc3() {
		// Fazer busca para achar path do .class
		Path rootPath = Paths.get(System.getProperty("user.dir"));
		String className = Thread.currentThread().getStackTrace()[2].getClassName();
		
		try {
			Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
				@Override
			    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {

			        if (file.toString().endsWith(className+".class")) {
			        	//System.out.println(file.toString());
			        	classPath = file.toString();
			        }
			        
			        return FileVisitResult.CONTINUE;
			    }
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	pointcut pc2(): cflow(execution(@Test * *.*(*))) &&  !within(asp);
	before(): pc2() {
		String regex = "[A-z]+\\s[A-z0-9-_$]+\\.[A-z0-9-_$]+\\([A-z0-9-_$,\\s]*\\)";
		String signature = thisJoinPoint.getSignature().toString();
		
		
		if (signature.matches(regex)){	// PEga metodo com assinatura valida
			//---------------------------------------------------------------------------
			// Extrai nome do metodo
			//---------------------------------------------------------------------------
			String methodName = CollectorExecutionFlow.extractClassName(signature);
			/*
			Pattern p = Pattern.compile("\\.[A-z0-9-_$]+\\(");
			Matcher m = p.matcher(signature);
			
			if (m.find()) {
				methodName = m.group();
				p = Pattern.compile("[A-z0-9-_$]+");
				m = p.matcher(methodName);
				if (m.find())
					methodName = m.group();
			}
			*/
			//---------------------------------------------------------------------------
			
			//---------------------------------------------------------------------------
			// Extrai tipos dos parâmetros do método (se houver)
			//---------------------------------------------------------------------------
			Class<?>[] paramTypes = CollectorExecutionFlow.extractParamTypes(thisJoinPoint.getArgs());
			
			/*
			if (thisJoinPoint.getArgs().length == 0) {	// parametro sem args
				paramTypes = null;
			} else {
				paramTypes = new Class<?>[thisJoinPoint.getArgs().length];
				
				int i = 0;
				for (Object o : thisJoinPoint.getArgs()) {
					paramTypes[i++] = o.getClass();
				}
			}
			*/
			//---------------------------------------------------------------------------
			
			
			//---------------------------------------------------------------------------
			// Coleta as info
			//---------------------------------------------------------------------------
			if (collector.containsKey(signature) && thisJoinPoint.getThis() != null) {	// Se método já existe, verifica se foi fornecida instancia
				ClassMethodInfo cmi = collector.get(signature);
				
				if (!cmi.hasInstance()) {	// Se metodo nao tiver instancia e foi fornecida uma, captura ela
					cmi.setInstance(thisJoinPoint.getThis());
				}
			} else {	// Metodo não foi coletado; coleta ele
				ClassMethodInfo cmi = new ClassMethodInfo(thisJoinPoint.getThis(), methodName, paramTypes, thisJoinPoint.getArgs());
				collector.put(signature, cmi);
			}			
		}
		
	}
	after(): pc3() {
		System.out.println("-----------------------------------------------------");
		new CollectorExecutionFlow(classPath, collector);
		System.out.println(classPath);
		System.out.println(collector);
		System.out.println();
	}
}

