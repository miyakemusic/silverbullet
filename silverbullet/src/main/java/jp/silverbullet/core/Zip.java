package jp.silverbullet.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Zip {
	   static final int BUFFER = 2048;
	   
	   public static void unzip(String zip, String destFolder) {
		      try {
		          final int BUFFER = 2048;
		          BufferedOutputStream dest = null;
		          FileInputStream fis = new 
		 	   FileInputStream(zip);
		          CheckedInputStream checksum = new 
		            CheckedInputStream(fis, new Adler32());
		          ZipInputStream zis = new 
		            ZipInputStream(new 
		              BufferedInputStream(checksum));
		          ZipEntry entry;
		          while((entry = zis.getNextEntry()) != null) {
		             int count;
		             byte data[] = new byte[BUFFER];
		             // write the files to the disk
		             FileOutputStream fos = new 
		               FileOutputStream(destFolder + "/" + entry.getName());
		             dest = new BufferedOutputStream(fos, 
		               BUFFER);
		             while ((count = zis.read(data, 0, 
		               BUFFER)) != -1) {
		                dest.write(data, 0, count);
		             }
		             dest.flush();
		             dest.close();
		          }
		          zis.close();
		       } catch(Exception e) {
		          e.printStackTrace();
		       }
	   }

	   public static void zip (String folder, String destFile) {
	      try {
	         BufferedInputStream origin = null;
	         FileOutputStream dest = new 
	           FileOutputStream(destFile);
	         ZipOutputStream out = new ZipOutputStream(new 
	           BufferedOutputStream(dest));
	         //out.setMethod(ZipOutputStream.DEFLATED);
	         byte data[] = new byte[BUFFER];
	         // get a list of files from current directory
	         File f = new File(folder);
	         File files[] = f.listFiles();//.list();

	         for (int i=0; i<files.length; i++) {
	            FileInputStream fi = new 
	              FileInputStream(files[i].getAbsolutePath());
	            origin = new 
	              BufferedInputStream(fi, BUFFER);
	            ZipEntry entry = new ZipEntry(files[i].getName());
	            out.putNextEntry(entry);
	            int count;
	            while((count = origin.read(data, 0, 
	              BUFFER)) != -1) {
	               out.write(data, 0, count);
	            }
	            origin.close();
	         }
	         out.close();
	      } catch(Exception e) {
	         e.printStackTrace();
	      }
	   }
	} 
