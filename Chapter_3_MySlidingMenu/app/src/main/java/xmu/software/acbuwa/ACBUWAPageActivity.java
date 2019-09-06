package xmu.software.acbuwa;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import xmu.software.acbuwa.adapter.ACBUWAListAdapter;
import xmu.software.acbuwa.adapter.MenuListAdapter;
import xmu.software.acbuwa.callback.SizeCallBackForMenu;
import xmu.software.acbuwa.ui.MenuHorizontalScrollView;

public class ACBUWAPageActivity extends Activity {

    private MenuHorizontalScrollView scrollView;
    private ListView menuList;
    private ListView acbuwaList;
    private ACBUWAListAdapter acbuwaListAdapter;
    private List<String> listItems;
    private View acbuwaPage;
    private Button menuBtn;
    private MenuListAdapter menuListAdapter;
    private View[] children;
    private LayoutInflater inflater;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        inflater = LayoutInflater.from(this);

        setContentView(inflater.inflate(R.layout.menu_scroll_view, null));
        this.scrollView = findViewById(R.id.mScrollView);
        this.menuListAdapter = new MenuListAdapter(this, 0);
        this.menuList = findViewById(R.id.menuList);
        this.menuList.setAdapter(menuListAdapter);

        this.init();
        this.acbuwaPage = inflater.inflate(R.layout.acbuwa_page, null);
        this.menuBtn = this.acbuwaPage.findViewById(R.id.menuBtn);
        this.acbuwaListAdapter = new ACBUWAListAdapter(this, this.listItems);
        this.acbuwaList = this.acbuwaPage
                .findViewById(R.id.acbuwa_list);
        this.acbuwaList.setAdapter(acbuwaListAdapter);
        this.menuBtn.setOnClickListener(onClickListener);

        View leftView = new View(this);
        leftView.setBackgroundColor(Color.TRANSPARENT);
        children = new View[]{leftView, acbuwaPage};
        this.scrollView.initViews(children, new SizeCallBackForMenu(
                this.menuBtn), this.menuList);
        this.scrollView.setMenuBtn(this.menuBtn);
        // this.scrollView.clickMenuBtn();
    }

    private OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            scrollView.clickMenuBtn();
        }
    };

    private void init() {
        this.listItems = new ArrayList<String>();
        for (int i = 0; i < 20; i++) {
            this.listItems.add("我要保研" + (i + 1));
        }
    }

    public MenuHorizontalScrollView getScrollView() {
        return scrollView;
    }

    public void setScrollView(MenuHorizontalScrollView scrollView) {
        this.scrollView = scrollView;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (MenuHorizontalScrollView.menuOut == true)
                this.scrollView.clickMenuBtn();
            else
                this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
