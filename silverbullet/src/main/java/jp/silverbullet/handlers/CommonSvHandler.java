package jp.silverbullet.handlers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.silverbullet.ChangedItemValue;
import jp.silverbullet.dependency.speceditor2.DependencyFormula;

public class CommonSvHandler extends AbstractSvHandler {

	private HandlerProperty handlerProperty;
	private Object object;
	private static Map<String, Object> instances = new HashMap<String, Object>();
	
	public CommonSvHandler(SvHandlerModel model, HandlerProperty handler) {
		super(model);
		this.handlerProperty = handler;
	}

	@Override
	protected void onExecute(final List<ChangedItemValue> list, final SvHandlerModel model2) {
	//	System.out.println("CommonSvHandler onExecute");
		
		// Run when values are changed. Ignores enabled/visible.
		boolean run = false;
		for (ChangedItemValue v : list) {
			if (v.element.equals(DependencyFormula.VALUE)) {
				run = true;
				break;
			}
		}
		if (!run) {
	//		System.out.println("CommonSvHandler onExecute return");
			return;
		}
	//	System.out.println(this.handlerProperty.getExternalClass());
		try {
			String className = getModel().getUserApplicationPath() + "."  + this.handlerProperty.getExternalClass().split("\\.")[0];
			String methodName = this.handlerProperty.getExternalClass().split("\\.")[1];
			object = getInstance(className);
			final Method method = object.getClass().getMethod(methodName, List.class, SvHandlerModel.class);

			// Async handlers
			if (this.handlerProperty.getAsync()) {
				new Thread() {
					@Override
					public void run() {
						try {
							method.invoke(object, list, model2);
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}.start();
			}
			else {
				method.invoke(object, list, model2);
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected Object getInstance(String className)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		if (!this.instances.keySet().contains(className)) {
			Class<?> c = Class.forName(className);
			Object object = c.newInstance();
			this.instances.put(className, object);
		}

		return this.instances.get(className);
	}
}
