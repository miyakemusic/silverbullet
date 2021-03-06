package jp.silverbullet.core.sequncer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import jp.silverbullet.core.BlobStore;
import jp.silverbullet.core.JsonPersistent;
import jp.silverbullet.core.dependency2.ChangedItemValue;
import jp.silverbullet.core.dependency2.Id;
import jp.silverbullet.core.dependency2.IdValue;
import jp.silverbullet.core.dependency2.RequestRejectedException;
import jp.silverbullet.core.property2.ChartProperty;
import jp.silverbullet.core.property2.IdValues;
import jp.silverbullet.core.property2.ImageProperty;
import jp.silverbullet.core.property2.RuntimeProperty;
import jp.silverbullet.core.property2.RuntimePropertyStore;
import jp.silverbullet.core.property2.TableProperty;
import jp.silverbullet.dev.BuilderInterface;
import jp.silverbullet.dev.PersistentHolder;

public abstract class LocalPersistent implements UserSequencer {

	private PersistentHolder persistentHolder;
	private RuntimePropertyStore store;
	private BlobStore blobStore;
//	private String application;
	abstract protected String application();
	
	public LocalPersistent(PersistentHolder persistentHolder, RuntimePropertyStore store, BlobStore blobStore) {
		
		this.persistentHolder = persistentHolder;
		this.store = store;
		this.blobStore = blobStore;
//		this.application = application;
	}

	@Override
	public void handle(SvHandlerModel model, Map<String, List<ChangedItemValue>> changed, Id sourceId)
			throws RequestRejectedException {


		for (String key : changed.keySet()) {
			String k = new Id(key).getId();
			if (!this.targetIds().contains(k)) {
				continue;
			}
			String id = new Id(key).getId();
			List<String> ids = persistentHolder.storedId(id);

			save(ids, getStorePath() + "/" + this.store.get(persistentHolder.path(id)).getCurrentValue());
		}
	}

	protected abstract String getStorePath();

	private void save(List<String> ids, String path) {

		
		String dir  =path.replace(new File(path).getName(), "");
		if (!Files.exists(Paths.get(dir))) {
			try {
				Files.createDirectories(Paths.get(dir));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		List<String> saved = new ArrayList<>();
		
		IdValues idValue = new IdValues(application());
		for (String id : ids) {
			RuntimeProperty prop = this.store.get(id);
			idValue.idValue.add(new IdValue(prop.getId(), prop.getCurrentValue()));
			if (blobStore.stores(id)) {
				Object obj = blobStore.get(id);
				if (obj.getClass().equals(ImageProperty.class)) {
					byte [] byte_array = ((ImageProperty)obj).getImage();
					ByteArrayInputStream input_stream= new ByteArrayInputStream(byte_array);
				      
					try {
						BufferedImage final_buffered_image = ImageIO.read(input_stream);
						String filename = path + "." + id + ".png";
						Files.deleteIfExists(Paths.get(filename));
						ImageIO.write(final_buffered_image , "png", new File(filename) );
						
						saved.add(filename);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else if (obj.getClass().equals(ChartProperty.class)) {
					try {
						String filename = path + "." + id + ".chart";
						Files.deleteIfExists(Paths.get(filename));
						Files.write(Paths.get(filename), obj.toString().getBytes(), StandardOpenOption.CREATE_NEW);
						
						saved.add(filename);
					} catch (IOException e) {
						e.printStackTrace();
					}					
				}
				else if (obj.getClass().equals(TableProperty.class)) {
					try {
						String filename = path + "." + id + ".table";
						Files.deleteIfExists(Paths.get(filename));
						Files.write(Paths.get(filename), obj.toString().getBytes(), StandardOpenOption.CREATE_NEW);
						saved.add(filename);
					} catch (IOException e) {
						e.printStackTrace();
					}					
				}
				else {
					try {
						String filename = path + "." + id;
						Files.deleteIfExists(Paths.get(filename));
						Files.write(Paths.get(filename), obj.toString().getBytes(), StandardOpenOption.CREATE_NEW);
						saved.add(filename);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			}
		}
		try {
			new JsonPersistent().saveJson(idValue, path + ".json");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		onSaved(saved);
	}

	protected abstract void onSaved(List<String> saved);

	private String createFolder(String path) {
		String concatPath = "";
		String[] tmp = path.split("/");
		for (int i = 0; i < tmp.length -1 ; i++) {
			String f = tmp[i];
			concatPath += f + "/";
			if (!new File(concatPath).exists()) {
				new File(concatPath).mkdir();
			}
		}
		return concatPath;
	}

	@Override
	public List<String> targetIds() {
		return persistentHolder.triggerList();
	}

}
