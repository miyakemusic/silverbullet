package jp.silverbullet.remote;

import javax.xml.bind.annotation.XmlRootElement;

import jp.silverbullet.remote.Title.Layout;
@XmlRootElement
public class SvTex implements Cloneable {
	@Override
	protected SvTex clone() {
		try {
			return (SvTex)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static final String PARAMSIGN = "$<$";

	private String min = "";
	private String max = "";
	private String section;
	private boolean async = false;
	private String asyncCompleteCondition = "";//"ID_SERVER_STATE.Value=ID_SERVER_STATE_IDLE";
	

	public String getAsyncCompleteCondition() {
		return asyncCompleteCondition;
	}

	public void setAsyncCompleteCondition(String asyncCompleteCondition) {
		this.asyncCompleteCondition = asyncCompleteCondition;
	}

	public boolean isAsync() {
		return async;
	}

	public void setAsync(boolean async) {
		this.async = async;
	}

	private boolean hasPair = false;

	private boolean parameterCountValid;

	private boolean parameterDescriptionValid;

	@Title(caption="Comment", height=3)
	private String comment;
	
	@Title(caption="Command", height=1)
	private String command = "";
	
	@Title(caption="Syntax", height=2, /*width = 400, */layout=Layout.Normal)
	private String syntax = "";
	
	@Title(caption="Description", height=3/*, layout=Layout.LeftHalf*/)
	private String description = "";
	
//	@Title(caption="Japanese", height=4, layout=Layout.RightHalf)
//	public String japaneseDescription = "";
	
	@Title(caption="Parameters", height=5)
	private String parameters = "";
	
	@Title(caption="Response", height=2)
	private String response = "";
	
	@Title(caption="Example", height=2)
	private String example = "";
	
	@Title(caption="Errors", height=2)
	private String errors = "";
	
	@Title(caption="Note", height=2)
	private String note = "";
	
	public SvTex(){}
	
	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
		this.validate();
	}

	public String getSyntax() {
		return syntax;
	}

	public void setSyntax(String syntax) {
		this.syntax = syntax;
		this.validate();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
		this.validate();
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
		this.validate();
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
		this.validate();
	}

	public String getExample() {
		return example;
	}

	public void setExample(String example) {
		this.example = example;
		this.validate();
	}

	public String getErrors() {
		return errors;
	}

	public void setErrors(String errors) {
		this.errors = errors;
		this.validate();
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
		this.validate();
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getExecid() {
		return execid;
	}

	public void setExecid(String execid) {
		this.execid = execid;
	}

	public String getVlist() {
		return vlist;
	}

	public void setVlist(String vlist) {
		this.vlist = vlist;
	}

	public boolean isParameterCountValid() {
		return parameterCountValid;
	}

	public boolean isParameterDescriptionValid() {
		return parameterDescriptionValid;
	}

	@Title(caption="PARAMS", height=2)
	private String params = "";
	
	@Title(caption="EXECID", height=1)
	private String execid = "";
	
	@Title(caption="VLIST", height=1)
	private String vlist = "";
	
	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
		this.validate();
	}	

	public boolean isQuery() {
		return command.endsWith("?");
	}
	
	public boolean validate() {
		parameterCountValid = checkParameterCount();
		parameterDescriptionValid = checkParameterDescription();
		return !isError();
	}
	
	public boolean isError() {
		boolean ret = false;
		ret |= parameterCountValid;
		ret |= parameterDescriptionValid;
		return ret;
	}

	enum Now{
		Defalult,
		Parameter,
		Description
	}
	
	private boolean checkParameterDescription() {
		Now now = Now.Defalult;
		for (String line : this.parameters.split("\n")) {
			if (line.contains(PARAMSIGN)) {
				if (now.equals(Now.Parameter)) {
					return true;
				}
				now = Now.Parameter;
			}
			else {
				now = Now.Description;
			}
		}
		if (now.equals(Now.Parameter)) {
			return true;
		}
		return false;
	}

	private boolean checkParameterCount() {
		int syntaxCount = this.syntax.split("[\\s,]+").length - 1;
		int paramCount = 0;
		for (String line : this.parameters.split("\n")) {
			if (line.contains(PARAMSIGN)) {
				paramCount++;
			}
		}
		if (syntaxCount == 0 && !this.params.isEmpty()) {
			return true;
		}
		return syntaxCount != paramCount;
	}

	public String getErrorMessage() {
		String ret = "";
		if (this.parameterCountValid) {
			ret += "Parameter count is wrong.\n";
		}
		if (this.parameterDescriptionValid) {
			ret += "Parameter Description is bad\n";
		}
		return ret;
	}

	public void setPair(boolean b) {
		this.hasPair = b;
	}

	public boolean isInclude() {
		return this.command.startsWith("m4_include");
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getComment() {
		if (comment == null) {
			comment = "";
		}
		return comment;
	}

	public String getMin() {
		return min;
	}

	public void setMin(String min) {
		this.min = min;
	}

	public String getMax() {
		return max;
	}

	public void setMax(String max) {
		this.max = max;
	}

	public boolean isHasPair() {
		return hasPair;
	}

	public void setHasPair(boolean hasPair) {
		this.hasPair = hasPair;
	}

	public void setParameterCountValid(boolean parameterCountValid) {
		this.parameterCountValid = parameterCountValid;
	}

	public void setParameterDescriptionValid(boolean parameterDescriptionValid) {
		this.parameterDescriptionValid = parameterDescriptionValid;
	}
	
}
