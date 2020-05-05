package jp.silverbullet.web.auth;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GoogleHandlerImpl implements ExternalStorageService {
	
	private String client_id;
	private String client_secret;
	private OkHttpClient httpClient;

	public GoogleHandlerImpl(Client client) {
		httpClient = new OkHttpClient();
		try {
			List<String> lines = Files.readAllLines(Paths.get("./code"));
			this.client_id = lines.get(0);
			this.client_secret = lines.get(1);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public GoogleAccressTokenResponse retrieveAccessToken(String code, String redirectUri) throws Exception {		
        Map<String, String> formParams = new HashMap<>();
      
        formParams.put("client_id", client_id);
        formParams.put("client_secret", client_secret);
        if (redirectUri != null) {
        	formParams.put("redirect_uri", redirectUri);
        }
        
        formParams.put("grant_type", "authorization_code");
        formParams.put("access_type", "offline");
        formParams.put("code", code);
        
        System.out.println("client_id=" + client_id);
        System.out.println("client_secret=" + client_secret);
        System.out.println("code=" + code);
        
        final FormBody.Builder formBuilder = new FormBody.Builder();
        formParams.forEach((k, v) -> formBuilder.add(k, v));
        RequestBody requestBody = formBuilder.build();
        System.out.println("Request request = new Request.Builder()");
        Request request = new Request.Builder()
                .url("https://www.googleapis.com/oauth2/v4/token")
                .post(requestBody)
                .build();


        System.out.println("OkHttpClient");
        Response responseOk = httpClient.newCall(request).execute();
        String json = responseOk.body().string();   
        
        System.out.println("Object Mapper");
        GoogleAccressTokenResponse response = new ObjectMapper().readValue(json, GoogleAccressTokenResponse.class);
        System.out.println("access token=" + response.access_token);
        return response;
	}

	@Override
	public PersonalResponse retrievePersonal(String accessToken) throws Exception {
        Request request = new Request.Builder()
                .url("https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=" + accessToken)
                .get()
                .build();
        Response responseOk = httpClient.newCall(request).execute();
        String string  = responseOk.body().string();
		return new ObjectMapper().readValue(string, PersonalResponse.class);	
	}

//	@Override
//	public String getAuthUri(String redirectUri) {
//		return "https://accounts.google.com/o/oauth2/auth?response_type=code&client_id=" + client_id + "&redirect_uri=" + redirectUri
//				+ "&scope=https://www.googleapis.com/auth/userinfo.profile&access_type=offline&approval_prompt=force";
//	}

	@Override
	public String getAuthUri(String redirectUri) {
		return "https://accounts.google.com/o/oauth2/auth?response_type=code&client_id=" + client_id + "&redirect_uri=" + redirectUri
				+ "&scope=https://www.googleapis.com/auth/userinfo.profile" + 
				"%20https://www.googleapis.com/auth/drive.appdata" + 
				"%20https://www.googleapis.com/auth/drive.file" + 
				"&access_type=offline&approval_prompt=force";
	}

//	@Override
//	public void postFile2(String access_token, String contentType, File file, String filename) {
//      final RequestBody requestBody = RequestBody.create(MediaType.parse(contentType), file);    
//		
//      Request request = new Request.Builder()
//              .url("https://www.googleapis.com/upload/drive/v3/files?uploadType=media")//&access_token=" + accessToken)
//              .addHeader("Authorization", "Bearer " + access_token)
//              .addHeader("Content-Type", contentType)
//              .addHeader("Content-Length", String.valueOf(file.length()))
//              .post(requestBody)
//              .build();
//      try {
//			Response responseOk = httpClient.newCall(request).execute();
//			System.out.println("Uploaded " + responseOk.toString());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}	
//	}
		
	private String useLibrary(Drive drive, String folderName, String parentFolderID) {
		
		com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
		fileMetadata.setName(folderName);
		fileMetadata.setMimeType("application/vnd.google-apps.folder");
		if (!parentFolderID.isEmpty()) {
			fileMetadata.setParents(Collections.singletonList(parentFolderID));
		}
		try {
			com.google.api.services.drive.model.File file = drive.files().create(fileMetadata)
			    .setFields("id")
			    .execute();
			return file.getId();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private String getFolderID(Drive drive, String folder, String parentFolderID) {
		
		try {
			String pageToken = null;
			 
			String query = "";
			if (parentFolderID.isEmpty()) {
				query = "name='" + folder + "' and trashed = false";
			}
			else {
				query = "name='" + folder + "' and trashed = false and '" + parentFolderID + "' in parents";
			}
			do {
			  FileList result = drive.files().list()
			      .setQ(query)
			      .setSpaces("drive")
			      .setFields("nextPageToken, files(id, name)")
			      .setPageToken(pageToken)
			      .execute();
			  for (com.google.api.services.drive.model.File file : result.getFiles()) {
				  System.out.printf("Found file: %s (%s)\n", file.getName(), file.getId());
				  if (file.getName().equals(folder)) {
					  return file.getId();
				  }
			    
			  }
			  pageToken = result.getNextPageToken();
			} while (pageToken != null);
		}
		catch (Exception e) {
			
		}
		return this.createApplicationFolder(drive, folder, parentFolderID);
	}
	private String createApplicationFolder(Drive drive, String folderName, String parentFolderID) {
		return useLibrary(drive, folderName, parentFolderID);
//		RequestBody requestBody = RequestBody.create(MediaType.get("application/json; charset=utf-8"), 
//				"{\"name\":\"" + folderName + "\", \"mimeType\":\"application/vnd.google-apps.folder\"}");
//		OkHttpClient client = new OkHttpClient();
//		Request request = new Request.Builder()
//              .url("https://www.googleapis.com/drive/v3/files")
//              .addHeader("Authorization", "Bearer " + access_token)
//              .addHeader("Content-Type", "application/json")
//              .post(requestBody)
//              .build();
//		try {
//			System.out.println(request.toString());
//			Response response = client.newCall(request).execute();
//			System.out.println("Uploaded " + response.toString());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}		
	}

	private JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();
	private NetHttpTransport transport = new NetHttpTransport();
	
	@Override
	public String postFile(String access_token, String contentType, String path, File file) {
		GoogleCredential credential = new GoogleCredential();
		credential.setAccessToken(access_token);
		
		Drive drive =
		    new Drive.Builder(transport, jsonFactory, credential)
		        .setApplicationName("doctorssns")
		        .build();
		
		String tmp[] = path.split("/");
		String parentFolderID = "";
		for (int i = 0; i < tmp.length-1; i++) {
			String folder = tmp[i];
			if (folder.isEmpty()) {
				continue;
			}
			parentFolderID = this.getFolderID(drive, folder, parentFolderID);
		}	
		
		try {
			String fileID = getFileID(drive, parentFolderID, file);
					
			if (fileID != null) {
				deleteFile(drive, fileID);
			}

			com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
			fileMetadata.setName(file.getName());
			fileMetadata.setParents(Collections.singletonList(parentFolderID));
			FileContent mediaContent = new FileContent(contentType, file);
			com.google.api.services.drive.model.File file2 = drive.files().create(fileMetadata, mediaContent)
			    .setFields("id, parents")
			    .execute();
			System.out.println("File ID: " + file2.getId());
			return file2.getId();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private void deleteFile(Drive drive, String fileID) {
		try {
			drive.files().delete(fileID).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getFileID(Drive drive, String folderId, File target) {
		try {
			String pageToken = null;
			do {
			  FileList result = drive.files().list()
			      .setQ("name='" + target.getName() + "' and trashed = false and '" + folderId + "' in parents")
			      .setSpaces("drive")
			      .setFields("nextPageToken, files(id, name)")
			      .setPageToken(pageToken)
			      .execute();
			  for (com.google.api.services.drive.model.File file : result.getFiles()) {
				  System.out.printf("Found file: %s (%s)\n", file.getName(), file.getId());
				  if (file.getName().equals(target.getName())) {
					  return file.getId();
				  }
			    
			  }
			  pageToken = result.getNextPageToken();
			} while (pageToken != null);
		}
		catch (IOException e) {
			
		}
		return null;
	}

	@Override
	public File download(String access_token, String fileid) {
		OutputStream outputStream = new ByteArrayOutputStream();
		
		GoogleCredential credential = new GoogleCredential();
		credential.setAccessToken(access_token);
		
		Drive drive =
		    new Drive.Builder(transport, jsonFactory, credential)
		        .setApplicationName("doctorssns")
		        .build();
		try {
			com.google.api.services.drive.model.File file = drive.files().get(fileid).execute();
			
			drive.files().get(fileid).executeMediaAndDownloadTo(outputStream);

			FileOutputStream fos = new FileOutputStream(new File(file.getName())); 
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();

		    // Put data in your baos

		    baos.writeTo(fos);
		    baos.close();
		    
		    File ret = new File(file.getName());
		    
		    Files.delete(Paths.get(file.getName()));
		    return ret;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	//	@Override
//	public void postFile3(String access_token, String contentType, File file) {
////		createApplicationFolder(access_token, "SilverBullet");
//		
//		OkHttpClient client = new OkHttpClient();
//		
//		RequestBody requestBody = new MultipartBody.Builder(/*"--foo_bar_baz"*/)
//		        .setType(MultipartBody.FORM)
//		        .addPart(RequestBody.create(MediaType.parse("application/json"), "{\"name\":\"" + file.getName() + "\"}"))
//		        .addPart(RequestBody.create(MediaType.parse(contentType), file))
//		        .build();
//
//		
//		Request request = new Request.Builder()
//              .url("https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart")
//              .addHeader("Authorization", "Bearer " + access_token)
//              .addHeader("Content-Type", contentType)
//              .addHeader("Content-Length", String.valueOf(file.length()))
//              .post(requestBody)
//              .build();
//		try {
//			System.out.println(request.toString());
//			Response response = client.newCall(request).execute();
//			System.out.println("Uploaded " + response.toString());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}	
}
