package jp.silverbullet.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import jp.silverbullet.BuilderModel;
import jp.silverbullet.ChangedItemValue;
import jp.silverbullet.IdValues;
import jp.silverbullet.SequencerListener;
import jp.silverbullet.SvProperty;
import jp.silverbullet.SvPropertyStore;
import jp.silverbullet.XmlPersistent;
import jp.silverbullet.dependency.engine.DependencyEngine;
import jp.silverbullet.dependency.engine.DependencyListener;
import jp.silverbullet.dependency.engine.RequestRejectedException;
import jp.silverbullet.register.RegisterMonitor;

import javax.xml.bind.JAXBException;

public class TestRecorder implements RegisterMonitor {
	
	private Set<TestRecorderListener> listeners = new HashSet<>();
	//private TableDataList tableDataList = new TableDataList();
	private ObservableList<TableData> tableData = FXCollections.observableArrayList();
	private TableData currentCommand;
	private String currentScriptName = "DefaultName";
	public ObservableList<TableData> getTableData() {
		return tableData;
	}

	private boolean playing = false;
	private BuilderModel builderModel;
	private boolean simulatorEnabled;
	
	public void setListener(TestRecorderListener listener) {
		this.listeners.add(listener);
	}
	
	public TestRecorder(BuilderModel builderModel) {	
		this.builderModel = builderModel;

		builderModel.getSequencer().addSequencerListener(new SequencerListener() {
			@Override
			public void onChangedBySystem(String id, String value) {
				if (playing)return;
				String s = "SYSTEM: " + id + "=" + value;
			}

			@Override
			public void onChangedByUser(String id, String value) {
				if (playing)return;
				
				currentCommand = new TableData();
				currentCommand.setTest(id + "=" + value);
				tableData.add(currentCommand);
			}
		});
		
		builderModel.getDependency().addDependencyListener(new DependencyListener() {
			@Override
			public boolean confirm(String history) {
				currentCommand.setDependency(currentCommand.getDependency() + "\n Confirm:" + history);
				updateTable();
				return true;
			}

			@Override
			public void onResult(
					Map<String, List<ChangedItemValue>> changedHistory) {
				String s = "";
				int i = 0;
				for (String key : changedHistory.keySet()) {
					if (i++ == 0) {
						continue;
					}
					for (ChangedItemValue v : changedHistory.get(key)) {
						s += " " + key + "." + v + ";";
					}
				}
				currentCommand.setDependency(s);
				updateTable();
			}
		});
	}

	private void updateTable() {
		TableData tableData = new TableData();
		tableData.setTest("'DUMMY");
		this.tableData.add(tableData);
		this.tableData.remove(tableData);
	}

	public void play() {
		playing = true;		
	
		new Thread() {
			@Override
			public void run() {
				for (int i = 0; i < tableData.size(); i++) {
					final TableData data = tableData.get(i);
					data.resetResult();
					String line = data.getTest();
					if (line.isEmpty() || line.trim().startsWith("'")) {
						continue;
					}
					String[] c = line.split("=");
					if (c.length < 2) {
						System.out.println(line);
					}
					final String cmd = c[0];
					final String prm = c[1];
					if (cmd.trim().isEmpty() || cmd.trim().startsWith("'")) {
						continue;
					}
					final boolean last = i == tableData.size()-1;
					
					if (cmd.equalsIgnoreCase("*WAIT")) {
						try {
							Thread.sleep(Integer.valueOf(prm));
							continue;
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					final int number = i;
					// Pass it to main thread
					Platform.runLater(new Runnable() {
						@Override
						public void run() {						
							currentCommand = data;
							fireTestProgress(number);
							if (cmd.startsWith("*")) {
								if (cmd.equalsIgnoreCase("*LOAD")) {
									loadSnapShot(prm);
								}
								else if (cmd.equalsIgnoreCase("*")) {
									
								}
							}
							else if (cmd.endsWith("?")) {
								doQuery(cmd, prm);
							}
							else {
								doCommand(cmd, prm);
							}
							if (last) {
								fireTestFinished();
							}
						}
					});
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	private void fireTestProgress(int number) {
		for (TestRecorderListener listener : listeners) {
			listener.onTestProgress(number);
		}
	}
	
	private void fireTestFinished() {
		for (TestRecorderListener listener : listeners) {
			listener.onTestFinished();
		}
	}
	
	public void loadFile(String prm) {
		IdValues data = loadSnapShot(prm);
		DependencyEngine dependency = new DependencyEngine() {
			@Override
			protected SvPropertyStore getPropertiesStore() {
				return builderModel.getPropertyStore();
			}
		};
		
		
		for (String id : data.getAllIds()) {
			String value = data.getValue(id);
			try {
				dependency.requestChange(id, value);
			} catch (RequestRejectedException e) {
				System.out.println("LOADING ERROR: " + e.getMessage().replace("\n", ""));
				//e.printStackTrace();
			}
		}
		this.tableData.clear();
		XmlPersistent<TableDataList> propertyPersister = new XmlPersistent<>();
		try {
			this.tableData.addAll(propertyPersister.load(prm + "/test.xml", TableDataList.class).getData());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	protected void doQuery(String id, String value) {
		String evalulation = "";
		id = id.replace("?", "");
		String result = builderModel.getProperty(id).getCurrentValue();
		if (result.equals(value)) {
			evalulation += "Pass";
		}
		else {
			evalulation += "Fail";
		}
		currentCommand.setPassFail(evalulation);
		currentCommand.setResult(result);
		updateTable();
//		fireUpdate(id + "=" + value, result, evalulation);
	}

	private void doCommand(final String id, final String value) {
		try {
			builderModel.getDependency().requestChange(id, value);
			
			//fireUpdate("Playing.. " + id + "=" + value, "", "");
		} catch (RequestRejectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void record(String scriptName) {
		this.playing = false;
		this.tableData.clear();
		
		TableData command = new TableData();
		command.setTest("*LOAD=" + scriptName);
		this.tableData.add(command);
		saveSnapShot(scriptName);
	}
	
	private IdValues loadSnapShot(String path) {
		XmlPersistent<IdValues> propertyPersister = new XmlPersistent<IdValues>();
		try {
			return propertyPersister.load(path + "/snapshot.xml", IdValues.class);	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void saveSnapShot(String path) {
		this.currentScriptName = path;
		try {
			Files.createDirectories(Paths.get(path));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		IdValues data = new IdValues();
		for (SvProperty prop : this.builderModel.getAllProperties()) {
			if (prop.isActionProperty()) {
				continue;
			}
			data.add(prop.getId(), prop.getCurrentValue());
		}
		
		XmlPersistent<IdValues> propertyPersister = new XmlPersistent<IdValues>();
		try {
			propertyPersister.save(data, path + "/snapshot.xml", IdValues.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public void stopRecord() {
		String path = this.currentScriptName;
		saveScript(path);
		fireRecordStopped();
	}

	protected void saveScript(String path) {
		TableDataList tableDataList = new TableDataList();
		tableDataList.getData().addAll(this.tableData.subList(0, this.tableData.size()));
		XmlPersistent<TableDataList> propertyPersister = new XmlPersistent<>();
		try {
			propertyPersister.save(tableDataList, path + "/test.xml", TableDataList.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	private void fireRecordStopped() {
		for (TestRecorderListener listener : listeners) {
			listener.onRecoredStopped();
		}
	}

	public void insert(int row) {
		this.tableData.add(row, new TableData());
	}

	public String getCurrentScriptName() {
		return currentScriptName;
	}

	public void stopPlay() {
		// TODO Auto-generated method stub
		
	}

	public void save(String text) {
		this.saveScript(text);
	}

	@Override
	public void writeIo(long address, BitSet data, BitSet mask) {
//		String d = data.toString();
//		String text = "write(0x" + Long.toHexString(address) + ")=" + d;
//		if (!currentCommand.getRegister().isEmpty()) {
//			currentCommand.setRegister(currentCommand.getRegister() + ";" + text);
//		}
//		else {
//			currentCommand.setRegister(text);
//		}
		TableData row = new TableData();
		row.setTest("*REG:writeIo (0x" + Long.toHexString(address) + ") = " + data + ":" +  mask);
		this.tableData.add(row);
		updateTable();
	}

	@Override
	public void updateBlock(long address, byte[] data) {
		System.out.println("updateBlock ");
		TableData row = new TableData();
		row.setTest("*REG:updateBlock(0x" + Long.toHexString(address) + ") = ");
		this.tableData.add(row);
		updateTable();
	}

	@Override
	public void updateIo(long address, BitSet data, BitSet mask) {
		//System.out.println("updateIo ");
		TableData row = new TableData();
		row.setTest("*REG:updateIo(0x" + Long.toHexString(address) + ") = ");
		this.tableData.add(row);
		updateTable();
	}

	@Override
	public void interrupt() {
//		System.out.println("interrupt ");
//		TableData row = new TableData();
//		row.setTest("interrupt");
//		this.tableData.add(row);
//		updateTable();
	}

	@Override
	public void setSimulator(String simulator) {
		TableData row = new TableData();
		row.setTest("*REG:SIMULATOR=" + simulator);
		this.tableData.add(row);
		updateTable();
	}

	@Override
	public void setSimulatorEnabled(boolean enabled) {
		TableData row = new TableData();
		row.setTest("*REG:SIMULATOR_ENABLED=" + enabled);
		this.tableData.add(row);
		updateTable();
		this.simulatorEnabled = enabled;
	}

	public void clear() {
		this.tableData.clear();
		updateTable();
	}

}
