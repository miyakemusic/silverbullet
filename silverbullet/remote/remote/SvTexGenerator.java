package jp.silverbullet.remote;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SvTexGenerator {
	private static final String TEX_LINEFEED = " \\\\";
	private String text = "";
	private SvTexHolder holder;
	
	public SvTexGenerator(SvTexHolder holder) {
		this.holder = holder;
	}
	
	public String generateToFile(String filename) {
		String ret = this.generate(holder);
		List<String> lines = Arrays.asList(ret.split("\r\n"));
		try {
			Files.write(Paths.get(filename), lines, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ret;
	}
	public String generate(SvTexHolder holder) {
		holder.analyzePair();
		this.text += "\\newpage" + this.getLineFeed();
		wirteChapter(holder);
		
		for (String section : holder.getSections()) {
			writeSection(section);
			boolean prevPair = false;
			for (SvTex tex : holder.getTexList(section)) {
				this.text += tex.getComment();
				if (tex.isInclude()) {
					writeInclude(tex);
				}
				else {
					writeOneCommand(tex, prevPair);
					prevPair = tex.isHasPair();
				}
			}
		}
		
		return text;
	}

	private void writeInclude(SvTex tex) {
		text += tex.getCommand() + this.getLineFeed();
	}

	private void wirteChapter(SvTexHolder holder) {
		text += "\\chapter{" + holder.getChapter() + "}" + getLineFeed();
		if (!holder.getHeader().isEmpty()) {
			text += holder.getHeader() + getLineFeed();
		}
	}

	private String getLineFeed() {
		return "\r\n";
	}
	private void writeSection(String section) {
		text += "\\section{" + section + "}" + getLineFeed();
	}

	private void writeOneCommand(SvTex tex, boolean prevPair) {
		if (!prevPair) {
			writeSubSection(tex);
		}
		boolean isLong = isLong(tex);
		writeBegin(isLong); // ctbegin
		writeSyntax(tex);
		writeContent(getDescriptionText(), tex.getDescription(), false);
		writeParameters(tex);
		writeContent(getResponseText(), tex.getResponse(), false);
		writeContent/*TextTT*/(getExampleText(), tex.getExample(), false);
		writeContent(getErrorsText(), tex.getErrors(), false);
		writeContent(getNoteText(), tex.getNote(), false);
		writeEnd(isLong, tex.isHasPair()); // ctend
		writeInternalParameters(tex);
	}

	protected String getSyntaxText() {
		return "Syntax";
	}
	
	protected String getDescriptionText() {
		return "Description";
	}
	
	protected String getParamtersText() {
		return "Parameters";
	}

	protected String getResponseText() {
		return "Response";
	}
	
	protected String getExampleText() {
		return "Example";
	}

	protected String getErrorsText() {
		return "Errors";
	}
	
	protected String getNoteText() {
		return "Note";
	}

	private boolean isLong(SvTex tex) {
		int lines = 0;
		lines += countLines(tex.getDescription());
		lines += countLines(tex.getParameters());
		lines += countLines(tex.getResponse());
		lines += countLines(tex.getExample());
		lines += countLines(tex.getErrors());
		lines += countLines(tex.getNote());
		return lines > 30;
	}

	private int countLines(String string) {
		return string.split("\n").length;
	}

	private void writeContentTextTT(String caption, String content, boolean cline) {
		this.text += new ContentGenerator() {
			@Override
			protected String getRightDecoration() {
				return "}";
			}

			@Override
			protected String getLeftDecoration() {
				return "\\texttt{";
			}
		}.get(caption, content, cline);
	}

	public void writeParameters(SvTex tex) {
//		if (!tex.min.isEmpty() && !tex.max.isEmpty()) {
//			tex.parameters += "\\minmaxdef{MINimum = " + tex.min + ", MAXimum = " + tex.max + "}\n";
//		}
		writeContent(getParamtersText(), tex.getParameters(), true);
	}

	public void writeContent(String caption, String content, boolean cline) {
		this.text += new ContentGenerator().get(caption, content, cline);
	}
	private void writeSyntax(SvTex tex) {
		this.text += "           \\textbf{" + getSyntaxText() + "}      & " + tex.getSyntax() +  TEX_LINEFEED + this.getLineFeed();
	}


	private void writeInternalParameters(SvTex tex) {
		if (!tex.getParams().isEmpty()) {
			this.text += "%%PARAMS=" + tex.getParams() + this.getLineFeed();
		}
		if (!tex.getExecid().isEmpty()) {
			this.text += "%%EXEID=" + tex.getExecid() + this.getLineFeed();
		}
		if (!tex.getVlist().isEmpty()) {
			this.text += "%%VLIST=" + tex.getVlist() + this.getLineFeed();
		}
	}
	private void writeEnd(boolean isLong, boolean hasPair) {
		if (isLong) {
			this.text += "\\cltend" + this.getLineFeed();
		}
		else {
			if (hasPair) {
				this.text += "\\ctendws" + this.getLineFeed();
			}
			else {
				this.text += "\\ctend" + this.getLineFeed();
			}
		}
	}
	
	private void writeBegin(boolean isLong) {
		if (isLong) {
			this.text += "\\cltbegin" + this.getLineFeed();
		}
		else {
			this.text += "\\ctbegin" + this.getLineFeed();
		}
	}
	private void writeSubSection(SvTex tex) {
		this.text += "\\subsection{" + tex.getCommand() + "}" + this.getLineFeed();
	}

	public void generateSource(String filename) {
		Set<String> ids = new HashSet<String>();
		Set<String> executors = new HashSet<String>();
		for (SvTex tex : this.holder.getAllTexs()) {
			ids.add(tex.getVlist());
			executors.add(tex.getExecid());
		}
		
		String ret = "";
		// Executor
		ret += "enum exeIDs\n{\n";
		for (String s: executors) {
			ret += "    " + s + "," + "\n";
		}
		ret = ret.substring(0, ret.length() -2)  + "\n";
		ret += "};\n";
		
		// ID
		ret += "enum ePropertyIDs\n{\n";
		for (String s: ids) {
			ret += "    " + s + "," + "\n";
		}
		ret = ret.substring(0, ret.length() -2) + "\n";
		ret += "};\n";
		
		//
		ret += "\n\n// ExecutorBase::ExecutorBase(OsaServerImpl* parent)\n\n";
		for (String s: ids) {
			ret += "m_ProPertyTable.insert DEFINITION_ELEMENT(" + s + ");\n";
		}
		try {
			FileWriter writer = new FileWriter(new File(filename));
			writer.write(ret);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	class ContentGenerator {
		public String get(String caption, String content, boolean cline) {
			String ret = "";
			String[] tmp = content.split("\n");
			String spaces = "";
			for (int i = 0; i < 12 - caption.length(); i++) {
				spaces += " ";
			}
			ret += "    \\hline \\textbf{" + caption + "}" + spaces + "& ";
			if (tmp.length > 1) {
				ret += decorate(tmp[0]) + TEX_LINEFEED + getLineFeed();
				for (int i = 1; i < tmp.length; i++) {

					if (cline && tmp[i].contains("$<$")) {
						ret += "    \\cline{2-2}                 & ";
					}
					else {
						ret += "                                & ";
					}
					ret += decorate(tmp[i]) + TEX_LINEFEED + getLineFeed(); 
				}
			}
			else {
				ret += decorate(content) + TEX_LINEFEED + getLineFeed();
			}
			return ret;
		}

		private String decorate(String content) {
			return getLeftDecoration()+ content + getRightDecoration();
		}

		protected String getRightDecoration() {
			return "";
		}

		protected String getLeftDecoration() {
			return "";
		}
	}
}
