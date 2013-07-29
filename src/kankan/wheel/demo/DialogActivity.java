package kankan.wheel.demo;

import com.sssta.ganmaqu.R;

import kankan.wheel.widget.ArrayWheelAdapter;
import kankan.wheel.widget.WheelView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DialogActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_layout);
		
		Button btn = (Button) findViewById(R.id.btn);
		btn.setOnClickListener(listener);
	}
	
	//初始化时汉字会挤压，在汉字左右两边添加空格即可
	private String[] names = {" 浙江 "," 江苏 "," 山东 "," 江西 "," 湖南 "," 广东 "};
	
	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			final AlertDialog dialog = new AlertDialog.Builder(DialogActivity.this).create();
			dialog.setTitle("选择分类");
			final WheelView catalogWheel = new WheelView(DialogActivity.this);
			dialog.setButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					// 实现下ui的刷新
				}
			});
			dialog.setButton2("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
		
			catalogWheel.setVisibleItems(5);
			catalogWheel.setCyclic(true);
			catalogWheel.setAdapter(new ArrayWheelAdapter<String>(names));
			dialog.setView(catalogWheel);
			dialog.show();
		}
	};
	
}
