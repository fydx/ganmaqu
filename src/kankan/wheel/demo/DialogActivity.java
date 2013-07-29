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
	
	//��ʼ��ʱ���ֻἷѹ���ں�������������ӿո񼴿�
	private String[] names = {" �㽭 "," ���� "," ɽ�� "," ���� "," ���� "," �㶫 "};
	
	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			final AlertDialog dialog = new AlertDialog.Builder(DialogActivity.this).create();
			dialog.setTitle("ѡ�����");
			final WheelView catalogWheel = new WheelView(DialogActivity.this);
			dialog.setButton("ȷ��", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					// ʵ����ui��ˢ��
				}
			});
			dialog.setButton2("ȡ��", new DialogInterface.OnClickListener() {
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
