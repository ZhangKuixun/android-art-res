package xmu.software.acbuwa.adapter;

import java.util.ArrayList;
import java.util.List;
import xmu.software.acbuwa.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;

public class ACBUWAListAdapter extends BaseAdapter {

    private List<String> listItems;
    private int itemCount;
    private LayoutInflater listInflater;
    private Context context;
    private RadioButton btn;

    public ACBUWAListAdapter(Context context, List<String> listItems) {
        // TODO Auto-generated constructor stub
        this.context = context;
        // this.dlg = dlg;
        this.listItems = new ArrayList<String>();
        this.listItems = listItems;
        this.init();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return this.itemCount;
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final int po = position;
        /* 与前台视图中的item做绑定 */
        if (convertView == null) {

            convertView = this.listInflater.inflate(R.layout.acbuwa_list_item,
                    null);
            this.btn = (RadioButton) convertView.findViewById(R.id.acbuwaRadio);
            convertView.setTag(this.btn);
        } else {
            this.btn = (RadioButton) convertView.getTag();
        }

        final String text = (String) this.listItems.get(position);
        this.btn.setText(text);

        return convertView;
    }

    /**
     * 初始化数据
     */
    private void init() {

        this.itemCount = this.listItems.size();
        this.listInflater = LayoutInflater.from(this.context);
    }
}
