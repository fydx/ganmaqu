package kankan.wheel.demo;

import kankan.wheel.widget.ArrayWheelAdapter;
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import android.app.Activity;
import android.os.Bundle;

import com.sssta.ganmaqu.R;

public class CitiesActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cities_layout);
                
        WheelView country = (WheelView) findViewById(R.id.country);
        String countries[] = new String[] {"我要我要", "我要我要", "Ukraine", "France"};
        country.setVisibleItems(3);
        country.setCyclic(true);//
        country.setAdapter(new ArrayWheelAdapter<String>(countries));
        String Types[] = new String[] {"亲子出行", "朋友聚会", "情侣约会"};
        final String cities[][] = new String[][] {
        		   Types,Types,Types,Types
        		};
        
        final WheelView city = (WheelView) findViewById(R.id.city);
        city.setVisibleItems(5);

        country.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				city.setAdapter(new ArrayWheelAdapter<String>(cities[newValue]));
				city.setCurrentItem(cities[newValue].length / 2);
			}
		});
        
        country.setCurrentItem(1);
    }
}
