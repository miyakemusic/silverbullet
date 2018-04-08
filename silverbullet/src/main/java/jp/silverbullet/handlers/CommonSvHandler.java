package jp.silverbullet.handlers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.silverbullet.dependency.speceditor3.ChangedItemValue2;
import jp.silverbullet.dependency.speceditor3.DependencyTargetElement;

public class CommonSvHandler extends AbstractSvHandler {

	private HandlerProperty handlerProperty;
	private Object object;
	private static Map<String, Object> instances = new HashMap<String, Object>();
	
	public CommonSvHandler(SvHandlerModel model, HandlerProperty handler) {
		super(model);
		this.handlerProperty = handler;
	}

	@Override
	protected void onExecute(final SvHandlerModel model2, Map<String, List<ChangedItemValue2>> changed) {
		
		// Run when values are changed. Ignores enabled/visible.
		boolean run = false;
		
		for (String id : this.handlerProperty.getIds()) {
			for (ChangedItemValue2 v : changed.get(id)) {
				if (v.getElement().equals(DependencyTargetElement.Value)) {
					run = true;
					break;
				}
			}
		}

		if (!run) {
			return;
		}
		try {
			String className = getModel().getUserApplicationPath() + "."  + this.handlerProperty.getExternalClass().split("\\.")[0];
			String methodName = this.handlerProperty.getExternalClass().split("\\.")[1];
			object = getInstance(className);
			final Method method = object.getClass().getMethod(methodName, SvHandlerModel.class, Map.class);

			// Async handlers
			if (this.handlerProperty.getAsync()) {
				new Thread() {
					@Override
					public void run() {
						try {
							method.invoke(object, model2, changed);
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
				method.invoke(object, model2, changed);
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
