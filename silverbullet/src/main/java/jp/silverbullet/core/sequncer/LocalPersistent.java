package jp.silverbullet.core.sequncer;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import jp.silverbullet.core.BlobStore;
import jp.silverbullet.core.JsonPersistent;
import jp.silverbullet.core.dependency2.ChangedItemValue;
import jp.silverbullet.core.dependency2.Id;
import jp.silverbullet.core.dependency2.IdValue;
import jp.silverbullet.core.dependency2.RequestRejectedException;
import jp.silverbullet.core.property2.IdValues;
import jp.silverbullet.core.property2.RuntimeProperty;
import jp.silverbullet.core.property2.RuntimePropertyStore;
import jp.silverbullet.dev.PersistentHolder;

public class LocalPersistent implements UserSequencer {

	private PersistentHolder persistentHolder;
	private RuntimePropertyStore store;
	private BlobStore blobStore;

	public LocalPersistent(PersistentHolder persistentHolder, RuntimePropertyStore store, BlobStore blobStore) {
		this.persistentHolder = persistentHolder;
		this.store = store;
		this.blobStore = blobStore;
	}

	@Override
	public void handle(SvHandlerModel model, Map<String, List<ChangedItemValue>> changed, Id sourceId)
			throws RequestRejectedException {

		for (String key : changed.keySet()) {
			String id = new Id(key).getId();
			List<String> ids = persistentHolder.storedId(id);
			save(ids, this.store.get(persistentHolder.path(id)).getCurrentValue());
		}
	}

	private void save(List<String> ids, String path) {
		String folder = createFolder(path);
		IdValues idValue = new IdValues();
		for (String id : ids) {
			RuntimeProperty prop = this.store.get(id);
			idValue.idValue.add(new IdValue(prop.getId(), prop.getCurrentValue()));
			if (blobStore.stores(id)) {
				Object obj = blobStore.get(id);
			//	ObjectOutputStream oos = new ObjectOutputStream();
				
			}
		}
		try {
			new JsonPersistent().saveJson(idValue, path + ".json");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

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
