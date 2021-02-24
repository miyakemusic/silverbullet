package jp.silverbullet.testspec;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;

import jp.silverbullet.core.SbDateTime;
import jp.silverbullet.core.property2.IdValues;

public class TestResultManager {

//	public static void main(String[] arg) {
//		new TestStatus();
//	}
	
	public TestResultManager() {
//		load("C:\\Users\\miyak\\OneDrive\\silverbullet\\Default00\\store");
	}
	FileTime fileTimeLatest = null;
	public TestResultManager load(String folder) {
		try {
			Files.list(Paths.get(folder)).filter(p -> Files.isDirectory(p)).forEach(project -> {
				
				try {
					fileTimeLatest = null;
					Files.list(project).forEach(file -> {
					
						try {
							FileTime fileTime = Files.getLastModifiedTime(file.toAbsolutePath());
							if (fileTimeLatest != null) {
								if (fileTime.compareTo(fileTimeLatest) > 0) {
									
								}
							}
							fileTimeLatest = fileTime;
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						String[] tmp = file.getFileName().toString().split("\\.");
						String nodeId = tmp[0];
						String portId = tmp[1];
						String direction = tmp[2];
						String side = tmp[3];
						String test = tmp[4];
						
						PortStatus portStatus = this.project(project.getFileName().toString()).node(nodeId).port(portId).direction(direction).side(side)
						.set(test);//, "Miyake");
						
						if (file.getFileName().toString().endsWith("json")) {
							try {
								IdValues obj = new ObjectMapper().readValue(file.toFile(), IdValues.class);
								String serialNumber = obj.value("ID_SYSTEM_SERIAL_NUMBER");
								portStatus.userName(serialNumber);
								
								String time = obj.value("ID_TEST_TIME");
								
								List<String> lines = Files.readAllLines(file);
								
								if (lines.toString().contains("_FAIL")) {
									portStatus.state(PortStateEnum.COMPLETE_FAIL, time);
								}
								else if (lines.toString().contains("_PASS")) {
									portStatus.state(PortStateEnum.COMPLETE_PASS, time);
								}
								else {
									portStatus.state(PortStateEnum.ON_GOING, time);
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

					});
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return this;
	}

	public PortStatus testState(String project, String nodeId, String portId, String direction, String side, String test) {
		return project(project).node(nodeId).port(portId).direction(direction).side(side);
	}

	private Map<String, Project> projects = new HashMap<>();
	public Project project(String project) {
		
		if (!projects.containsKey(project)) {
			projects.put(project, new Project());
		}
		return projects.get(project);
	}
	
	class LatestPort {
		public LatestPort(String portId2, Date date2) {
			this.portId = portId2;
			this.date = date2;
		}
		String portId;
		Date date;
	}
	public class Project {
		private Map<String, Node> nodes = new HashMap<>();
		private Map<String, LatestPort> latest = new HashMap<>();
		
		public Node node(String nodeId) {
			if (!this.nodes.containsKey(nodeId)) {
				this.nodes.put(nodeId, new Node() {
					@Override
					protected void onUpdate(String portId, String direction, String side, PortStatus portStatus) {
						Date date = new SbDateTime().date(portStatus.time);
						if (latest.containsKey(portStatus.userName)) {
							if (date.after(latest.get(portStatus.userName).date)) {
								latest.put(portStatus.userName, new LatestPort(portId, date));
							}
						}
						else {
							latest.put(portStatus.userName, new LatestPort(portId, date));
						}
					}
				});
			}
			return this.nodes.get(nodeId);
		}

		public List<JsPortStatus> summary() {
			List<JsPortStatus> ret = new ArrayList<>();
			List<String> portIds = new ArrayList<>();
			for (LatestPort lp : this.latest.values()) {
				portIds.add(lp.portId);
			}
			
			Map<String, PortStateEnum> summary = new HashMap<>();
			this.nodes.forEach( (nodeId, node)-> {
				summary.putAll(node.summary());
			});
			
			for (String portId : summary.keySet()) {
				ret.add(new JsPortStatus(portId, summary.get(portId), portIds.contains(portId)));
			}
			return ret;
		}

		public boolean isOnGoing(String portId) {
			for (LatestPort lp : this.latest.values()) {
				if (lp.portId.equals(portId)) {
					return true;
				}
			}
			return false;
		}
		
		
	}
	abstract class Node {
		private Map<String, Port> ports = new HashMap<>();
		
		public Port port(String portId) {
			if (!this.ports.containsKey(portId)) {
				this.ports.put(portId, new Port() {
					@Override
					protected void onUpdate(String direction, String side, PortStatus portStatus) {
						Node.this.onUpdate(portId, direction, side, portStatus);
					}
					
				});
			}
			return this.ports.get(portId);
		}

		public Map<String, PortStateEnum> summary() {
			Map<String, PortStateEnum> summary = new HashMap<>();
			
			this.ports.forEach((portId, port) -> {
				summary.put(portId, port.summary());
			});
			return summary;
		}

		protected abstract void onUpdate(String portId, String direction, String side, PortStatus portStatus);
	}
	abstract class Port {
		private Map<String, PortDirection> portDirections = new HashMap<>();
		
		public PortDirection direction(String direction) {
			if (!this.portDirections.containsKey(direction)) {
				this.portDirections.put(direction, new PortDirection() {
					@Override
					protected void onUpdate(String side, PortStatus portStatus) {
						Port.this.onUpdate(direction, side, portStatus);
					}
					
				});
			}
			return this.portDirections.get(direction);
		}
		
		public PortStateEnum summary() {
			Set<PortStateEnum> set = new HashSet<>();
			
			for (PortDirection portDirection : this.portDirections.values()) {
				for (PortStatus status : portDirection.portSides.values()) {
					if (status.state.compareTo(PortStateEnum.COMPLETE_FAIL) == 0) {
						return PortStateEnum.COMPLETE_FAIL;
					}
					set.add(status.state);
				}
			}

			if (set.contains(PortStateEnum.ON_GOING)) {
				return PortStateEnum.ON_GOING;
			}
			else if (set.contains(PortStateEnum.COMPLETE_PASS)) {
				return PortStateEnum.COMPLETE_PASS;
			}

			return PortStateEnum.NOT_TESTED;
		}
		protected abstract void onUpdate(String direction, String side, PortStatus portStatus);
	}
	
	abstract class PortDirection {
		private Map<String, PortStatus> portSides = new HashMap<>();
		public PortStatus side(String side) {
			if (!this.portSides.containsKey(side)) {
				this.portSides.put(side, new PortStatus() {
					@Override
					void onUpdate(PortStatus portStatus) {
						PortDirection.this.onUpdate(side, portStatus);
					}					
				});
			}
			return this.portSides.get(side);
		}

		protected abstract void onUpdate(String side, PortStatus portStatus);
	}
	
	abstract class PortStatus {
		private String testName;
		private PortStateEnum state = PortStateEnum.NOT_TESTED;
		private String userName;
		private String time;
		
		public PortStatus set(String testName2, String userName2) {
			this.testName = testName2;
			this.userName = userName2;
			return this;
		}
		
		public PortStatus userName(String serialNumber) {
			this.userName = serialNumber;
			return this;
		}

		public PortStatus set(String testName2) {
			this.testName = testName2;
			return this;
		}
		
		public PortStatus state(PortStateEnum state2, String time2) {
			this.state = state2;
			this.time = time2;
			onUpdate(this);
			return this;
		}
		
		abstract void onUpdate(PortStatus portStatus);
		
	}

}
