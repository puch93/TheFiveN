package kr.co.core.thefiven.utility;

import android.app.Activity;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;

public class BackPressCloseHandler {
	
	private long backKeyPressedTime = 0;
	private Toast toast;
	private Activity act;
	
	public BackPressCloseHandler(Activity activity ){
		this.act = activity;
	}
	
	public void onBackPressed() {
		if(System.currentTimeMillis() > backKeyPressedTime + 2000) {
			backKeyPressedTime = System.currentTimeMillis();
			showGuide();
			return;
		}
		
		if(System.currentTimeMillis() <= backKeyPressedTime + 2000) {
			logout();

			act.moveTaskToBack(true);
			act.finish();
			
			toast.cancel();
		}
	}
	private void logout() {
		ReqBasic server = new ReqBasic(act, NetUrls.LOGOUT) {
			@Override
			public void onAfter(int resultCode, HttpResult resultData) {
				if (resultData.getResult() != null) {
					try {
						JSONObject jo = new JSONObject(resultData.getResult());

						if( StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {

						} else {

						}

					} catch (JSONException e) {
						e.printStackTrace();
						Common.showToastNetwork(act);
					}
				} else {
					Common.showToastNetwork(act);
				}
			}
		};

		server.setTag("Logout");
		server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
		server.execute(true, false);
	}

	
	private void showGuide() {
		toast = Toast.makeText(act, "뒤로 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
		toast.show();
	}

}
