package xmu.software.acbuwa.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xmu.software.acbuwa.ACBUWAPageActivity;
import xmu.software.acbuwa.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * MenuList的适配器
 * @author acbuwa
 *
 */
public class MenuListAdapter extends BaseAdapter {
	
	private Activity context;
	private List<Map<String, Object>> listItems;
	private int itemCount;
	private LayoutInflater listInflater;
	private boolean isPressed[];
	private int imageId = R.drawable.star_icon;
	private String textContent = "i love xmu";
	private final int COUNT = 21; 
	private int pressedId;
	
	/*一个menu item中包含一个imageView和一个TextView*/
	public final class ListItemsView{
		public ImageView menuIcon;
		public TextView menuText;
	}
	
	
	public MenuListAdapter(Activity context, int pressedId) {
		// TODO Auto-generated constructor stub
//		switch(pressedId){
//		case 0:
//			this.context = (ACBUWAPageActivity)context;
//		case 1:
//			this.context = (CAROLPageActivity)context;
//		}
		this.context = context;
		this.pressedId = pressedId;
		this.init();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.itemCount;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
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
		ListItemsView listItemsView;
		if(convertView == null){
			listItemsView = new ListItemsView();
			convertView = this.listInflater.inflate(R.layout.menu_list_item, null);
			listItemsView.menuIcon = (ImageView)convertView.findViewById(R.id.menuIcon);
			listItemsView.menuText = (TextView)convertView.findViewById(R.id.menuText);
			convertView.setTag(listItemsView);
		}
		else{
			listItemsView = (ListItemsView)convertView.getTag();
		}
		
		listItemsView.menuIcon.setBackgroundResource((Integer)listItems.get(position).get("menuIcon"));
		listItemsView.menuText.setText((String)listItems.get(position).get("menuText"));
		
		if(this.isPressed[position] == true)
			convertView.setBackgroundResource(R.drawable.menu_item_bg_sel);
		else
			convertView.setBackgroundColor(Color.TRANSPARENT);
		
		convertView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				changeState(po);
				gotoActivity(po);
				notifyDataSetInvalidated();
				new Handler().post(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						
					}
					
				});
			}
		});
		
		return convertView;
	} 
	
	private void gotoActivity(int position){
		Intent intent = new Intent();
		switch(position){
		case 0:
			
			if(this.pressedId == 0){
				ACBUWAPageActivity activity = (ACBUWAPageActivity)context;
				activity.getScrollView().clickMenuBtn();
			}
			else{
				intent.setClass(context, ACBUWAPageActivity.class);
				context.startActivity(intent);
				context.overridePendingTransition(R.anim.push_in,R.anim.push_out);
				context.finish();
			}
			
			break;
		/*----------------------------------------------------*/
		default:
		}
	}
	
	private void changeState(int position){
		
		for(int i = 0; i < this.itemCount; i++){
			isPressed[i] = false;
		}
		isPressed[position] = true;
	}
	
	private void init(){
		
		this.itemCount = this.COUNT;
		this.listItems =  new ArrayList<Map<String, Object>>();
		this.isPressed = new boolean[this.itemCount];
		for(int i = 0; i < this.itemCount; i++){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("menuIcon", imageId);
			if(i == 0){
				map.put("menuText", "i am acbuwa");
			}
			else if(i == 1){
				map.put("menuText", "i love carol");
			}
			else{
				map.put("menuText", textContent);
			}
			this.listItems.add(map);
			this.isPressed[i] = false;
		}
		this.isPressed[this.pressedId] = true;
		this.listInflater = LayoutInflater.from(context); 
	}
}
