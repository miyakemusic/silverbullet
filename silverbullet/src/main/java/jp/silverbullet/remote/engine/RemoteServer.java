package jp.silverbullet.remote.engine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import jp.silverbullet.dependency2.RequestRejectedException;
import jp.silverbullet.property.SvProperty;
import jp.silverbullet.remote.SvTexHolder;
import jp.silverbullet.remote.commands.ClearCommandExecutor;
import jp.silverbullet.remote.commands.CommandCompleteCommandExecutor;
import jp.silverbullet.remote.commands.ErrorCommandExecutor;

public class RemoteServer {
	private SvSocketHandler remoteHandler = new SvSocketHandlerImpl();
	private RemoteServerModel model;
	private LinkedList<RemoteError> errors = new LinkedList<>();
	private List<AbstractCommandExecutor> commands = new ArrayList<>();
	private SyncController syncController = new SyncController();
	
	private RemoteCommandDi di = new RemoteCommandDi() {
		@Override
		public LinkedList<RemoteError> getErrors() {
			return errors;
		}

		@Override
		public void waitComplete() {
			syncController.waitComplete();
		}

		@Override
		public void clearSyncCondition() {
			syncController.clearAsyncCondition();
		}

		@Override
		public SvTexHolder getTexHolder() {
			return model.getTexHolder();
		}

		@Override
		public void addError(int i, String message) {
			errors.offer(new RemoteError(i, message));
		}

		@Override
		public SvProperty getProperty(String id) {
			return model.getProperty(id);
		}

		@Override
		public void setAsyncCondition(String asyncCompleteCondition) {
			syncController.setAsyncCondition(asyncCompleteCondition);
		}

		@Override
		public void requestChange(String id, String value) throws RequestRejectedException {
//			model.getDependency().requestChange(id, value);
		}

		@Override
		public SyncController getSyncController() {
			return syncController;
		}

		
	};	
	public RemoteServer(RemoteServerModel remoteServerModel) {
		model = remoteServerModel;
		
		this.commands.add(new ErrorCommandExecutor(di)); // *ERR?
		this.commands.add(new CommandCompleteCommandExecutor(di)); // *OPC?
		this.commands.add(new ClearCommandExecutor(di)); // *CLS
		this.commands.add(new SimpleSetGetExecutor(di)); // Auto Remote Command handler
		
        SvSocketServer remoteServer = new SvSocketServer(56001, remoteHandler);
        remoteServer.start();
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
//			try {
//				model.getDependency().requestChange(vlist, value);
//			} 
//			catch (RequestRejectedException e) {
//				e.printStackTrace();
//				errors.offer(new RemoteError(0, e.getMessage()));
//			}
//			finally {
//				onCompleted();
//			}
		}
	}

	class SvSocketHandlerImpl implements SvSocketHandler {		
		@Override
		public String onReceived(String line) {	
			// In Remote thread
			String[] tmp = line.split("\\s+");
			String command = tmp[0];

			syncController.reset();
			
			for (AbstractCommandExecutor commandHandler : commands) {
				if (commandHandler.matches(command)) {
					List<String> params = new ArrayList<>();
					for (int i = 1; i < tmp.length; i++) {
						params.add(tmp[i]);
					}
					return commandHandler.execute(command, params);
				}
			}
			
			return "";
		}

		@Override
		public boolean isQuery(String line) {
			return line.contains("?");
		}
		
	}
}
