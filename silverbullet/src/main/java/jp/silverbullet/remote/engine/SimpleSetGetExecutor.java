package jp.silverbullet.remote.engine;

import java.util.List;

import javafx.application.Platform;
import jp.silverbullet.SvProperty;
import jp.silverbullet.SvPropertyListener;
import jp.silverbullet.dependency.engine.RequestRejectedException;
import jp.silverbullet.dependency.speceditor2.DependencyFormula;
import jp.silverbullet.remote.SvTex;

public class SimpleSetGetExecutor extends AbstractCommandExecutor implements SvPropertyListener {

private String value;

//	private SyncController syncController = new SyncController();
	
	public SimpleSetGetExecutor(RemoteCommandDi model2) {
		super(model2, "");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String execute(String command, List<String> params) {
		final SvTex tex = find(command);
		
		if (tex == null) {
			//errors.offer(new RemoteError(1, "NO SUCH COMMAND!"));
			getModel().addError(1, "NO SUCH COMMAND!");
			return "";
		}
		
		if (tex.isAsync()) {
			String aSyncId = tex.getAsyncCompleteCondition().split(".Value")[0];
			getModel().getProperty(aSyncId).addListener(this);
			getModel().setAsyncCondition(tex.getAsyncCompleteCondition());
		}
		value = "";//params.get(0);
		
		SvProperty property = getModel().getProperty(tex.getVlist());
		
		if (!tex.isQuery()) {
			if (property.isActionProperty()) {
				value = DependencyFormula.ANY;
			}
			else if (property.isListProperty()) {
				if (params.size() == 1) {
					value = getListElementId(tex, params.get(0));
				}
				else {
					getModel().addError(2, "Invalid Argument Count");
				}
			}
			else if (property.isNumericProperty()) {
				value = params.get(0);
				if (value.equals("MIN")) {
					value = property.getMin();
				}
				else if (value.equals("MAX")){
					value = property.getMax();
				}
			}
			else {
				if (params.size() >= 1) {
					value = params.get(0);
				}
			}
			Platform.runLater(new FxRunnable(tex.getVlist(), value){
				@Override
				void onCompleted() {
					getModel().getSyncController().notifyComplete();
				}
			});

			if (!getModel().getSyncController().isCompleted()) {
				getModel().getSyncController().waitComplete();
			}
			else {
				
			}
		}
		else {
			return getReply(tex, value);
		}
		

		return "";
	}

	public SvTex find(String command) {
		for (SvTex tex : getModel().getTexHolder().getAllTexs()) {
			if (matchesScpi(command, tex.getCommand())) {
				return tex;
			}
		}
		return null;
	}

	public String getListElementId(SvTex tex, String value) {
		for (String s : getListParamsList(tex)) {
			String[] tmp = s.split("=");
			String def = tmp[0];
			String mandatory = getMandatory(tmp[0]);
			if (mandatory.length() > value.length()) {
				return "";
			}
			if (def.substring(0, value.length()).toUpperCase().equals(value.toUpperCase())) {
				return tmp[1];
			}
//			if (mandatory.equals(value)) {
//				return tmp[1];
//			}
		}
		return "";
	}
	
	protected String[] getListParamsList(SvTex tex) {
		return tex.getParams().replace("{", "").replace("}", "").split(",");
	}

	public String getReply(SvTex tex, String value2) {
		SvProperty prop = getModel().getProperty(tex.getVlist());
		if (prop.isListProperty()) {
			return getListElementValue(tex, prop.getCurrentValue());
		}
		else {
			return getModel().getProperty(tex.getVlist()).getCurrentValue();
		}
	}
	
	private String getListElementValue(SvTex tex, String currentValue) {
		for (String s : getListParamsList(tex)) {
			String[] tmp = s.split("=");
			if (tmp[1].equals(currentValue)) {
				return tmp[0];
			}
		}
		return "";
	}
	
	abstract class FxRunnable implements Runnable {
		abstract void onCompleted();
		
		private String vlist;
		private String value;

		public FxRunnable(String vlist2, String value2) {
			this.vlist = vlist2;
			this.value = value2;
		}

		@Override
		public void run() {
			try {
				getModel().requestChange(vlist, value);
			} 
			catch (RequestRejectedException e) {
				e.printStackTrace();
				//errors.offer(new RemoteError(0, e.getMessage()));
				getModel().addError(0, e.getMessage());
			}
			finally {
				onCompleted();
			}
		}
	}
	
	@Override
	public boolean matches(String command2) {
		for (SvTex tex : getModel().getTexHolder().getAllTexs()) {
			if (matchesScpi(command2, tex.getCommand())) {
				return true;
			}
		}
		return false;
	}

	protected boolean matchesScpi(String sentCommand, String texCommand) {
		String[] sentTemp = sentCommand.split(":");
		String[] texTemp = texCommand.split(":");
		if (sentTemp.length != texTemp.length) {
			return false;
		}
		if (sentCommand.endsWith("?") && !texCommand.endsWith("?")) {
			return false;
		}
		for (int i = 0; i < sentTemp.length; i++) {
			String sentPart = sentTemp[i].toUpperCase().replace("?", "");
			String texPart = texTemp[i].toUpperCase().replace("?", "");;
			String texMandatoryPart = getMandatory(texTemp[i]).toUpperCase().replace("?", "");;
			if (!sentPart.startsWith(texMandatoryPart) || !matchesWholeText(sentPart, texPart)) {
				return false;
			}
		}
		return true;
	}

	protected boolean matchesWholeText(String sentPart, String texPart) {
		if (sentPart.length() > texPart.length()) {
			return false;
		}
		return sentPart.equals(texPart.substring(0, sentPart.length()));
	}

	private String getMandatory(String string) {
		String ret = "";
		for (int i = 0; i < string.length(); i++) {
			Character c = string.charAt(i);
			if (Character.isUpperCase(c)) {
				ret += c;
			}
		}
		return ret;
	}

	@Override
	public void onValueChanged(String id, String value) {
		if (getModel().getSyncController().isWaitingAsyncCompletion()) {
			if (getModel().getSyncController().matchesSyncCompletion(id, value)) {
				getModel().getSyncController().notifyComplete();
				getModel().getProperty(id).removeListener(this);
			}
		}
	}

	@Override
	public void onEnableChanged(String id, boolean b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFlagChanged(String id, Flag flag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onVisibleChanged(String id, Boolean b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onListMaskChanged(String id, String string) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTitleChanged(String id, String title) {
		// TODO Auto-generated method stub
		
	}

}
