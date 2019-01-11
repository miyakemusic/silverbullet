package jp.silverbullet.remote;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class SvTexHtml {
	public void generateFile(String filename, SvTexHolder holder) {
		try {
			if (Files.exists(Paths.get(filename))) {
				Files.delete(Paths.get(filename));
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		holder.analyzePair();
		String text = "<HTML>";
		text += "<HEAD>";
		text += "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
		text += "</HEAD>";
		text += "<BODY>";
		for (SvTex tex : holder.getAllTexs()) {
			text += "<h2>" + tex.getCommand() + "</h2>";
			text += "<TABLE border=1 width=800>";
			text += "<TR><TD width=200>Syntax</TD>";
			text += "<TD>" + toHtml(tex.getSyntax()) + "</TD></TR>";
			text += "<TR><TD>Descriptions</TD>";
			text += "<TD>" + toHtml(tex.getDescription()) + "</TD></TR>";
			text += "<TR><TD>Parameters</TD>";
			text += "<TD>" +toHtml(tex.getParameters()) + "</TD></TR>";
			text += "<TR><TD>Response</TD>";
			text += "<TD>" +toHtml(tex.getResponse()) + "</TD></TR>";
			text += "<TR><TD>Example</TD>";
			text += "<TD>" + toHtml(tex.getExample()) + "</TD></TR>";
			text += "<TR><TD>Errors</TD>";
			text += "<TD>" + toHtml(tex.getErrors()) + "</TD></TR>";
			text += "<TR><TD>Note</TD>";
			text += "<TD>" + toHtml(tex.getNote()) + "</TD></TR>";
			text += "</TABLE>";
		}
		text += "</BODY></HTML>";
		try {
			Files.write(Paths.get(filename), 
					Arrays.asList(text.split("\n")), 
					Charset.forName("UTF-8"), StandardOpenOption.CREATE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String toHtml(String text2) {
		String ret = "";
		for (String text : text2.split("\n")) {
			if (!ret.isEmpty()) {
				ret += "<br>";
			}
			String line = "";
			line = text.replace("\n", "<BR>").replace("$<$", "&lt;").replace("$>$", "&gt;");
			if (line.contains("\\minmaxdef") || line.contains("\\texttt")) {
				line = line.split("[\\{\\}]+")[1];
			}
			line = line.replace("$\\rightarrow$", "->");
			ret += line;
		}
		return ret;
	}
}
