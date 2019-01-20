package mypackage;
import jp.silverbullet.dependency2.RequestRejectedException;
import jp.silverbullet.sequncer.EasyAccessInterface;
public class UserEasyAccess {
    private EasyAccessInterface model;
    public UserEasyAccess(EasyAccessInterface model2) {
        this.model = model2;
    }
    public enum EnumList{
        ID_LIST_A,
        ID_LIST_D,
    };
    public void setList(EnumList value) throws RequestRejectedException {
        model.requestChange(ID.ID_LIST, value.toString());
    }
    public EnumList getList() {
        return EnumList.valueOf(model.getProperty(ID.ID_LIST).getCurrentValue());
    }
    public void setNumeric(Double value) throws RequestRejectedException {
        model.requestChange(ID.ID_NUMERIC, String.valueOf(value));
    }
    public Double getNumeric() {
        return Double.valueOf(model.getProperty(ID.ID_NUMERIC).getCurrentValue());
    }
    public void setBool(Boolean value) throws RequestRejectedException {
        model.requestChange(ID.ID_BOOL, String.valueOf(value));
    }
    public Boolean getBool() {
        return Boolean.valueOf(model.getProperty(ID.ID_BOOL).getCurrentValue());
    }
    public void setText(String value) throws RequestRejectedException {
        model.requestChange(ID.ID_TEXT, String.valueOf(value));
    }
    public String getText() {
        return String.valueOf(model.getProperty(ID.ID_TEXT).getCurrentValue());
    }
    public void setTable(String value) throws RequestRejectedException {
        model.requestChange(ID.ID_TABLE, String.valueOf(value));
    }
    public String getTable() {
        return String.valueOf(model.getProperty(ID.ID_TABLE).getCurrentValue());
    }
}
