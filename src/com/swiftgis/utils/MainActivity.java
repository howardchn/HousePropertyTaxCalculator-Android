package com.swiftgis.utils;

import java.util.ArrayList;
import java.util.HashMap;

import com.wooboo.adlib_android.WoobooAdView;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MainActivity extends Activity {
	private WoobooAdView ad;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ad = (WoobooAdView) this.findViewById(R.id.ad);
		ad.setBackgroundColor(getResources().getColor(R.color.black));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId() == R.id.menu_exit) {
			this.finish();
		}

		return super.onMenuItemSelected(featureId, item);
	}

	public void onSubmitPrice(View mainActivity) {
		EditText editTextArea = (EditText) findViewById(R.id.editTextArea);
		EditText editTextPrice = (EditText) findViewById(R.id.editTextPrice);
		TextView result = (TextView) findViewById(R.id.textViewResult);
		ListView detailChargeList = (ListView) findViewById(R.id.listView1);

		try {
			double area = Double.parseDouble(editTextArea.getText().toString());
			double price = Double.parseDouble(editTextPrice.getText()
					.toString());
			ArrayList<HashMap<String, String>> chargeDetail = new ArrayList<HashMap<String, String>>();

			double total = getResult(area, price, chargeDetail);
			SimpleAdapter chargeDetailAdapter = new SimpleAdapter(this,
					chargeDetail, R.layout.charge_detail_item, new String[] {
							"textViewName", "textViewPrice" }, new int[] {
							R.id.textViewName, R.id.textViewPrice });

			detailChargeList.setAdapter(chargeDetailAdapter);
			result.setText(String.valueOf(total));
		} catch (Exception ex) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("错误");
			dialog.setMessage("输入有误, 请重新输入.");
			dialog.show();
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_ENTER) {
			onSubmitPrice(null);
			return false;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
		}

		return super.onKeyUp(keyCode, event);
	};

	private double getResult(double area, double price,
			ArrayList<HashMap<String, String>> chargeDetail) {
		double sellerTaxRatio = .01;
		double sellerChargePerSquare = 2.5;
		double buyerTaxRatio = .01;
		if (area > 90)
			buyerTaxRatio = .015;
		double buyerChargePerSquare = 2.5;
		double tradeRegister = 80;
		double mortgageRegister = 80;
		double pic = 25;
		double stampTax = 5;

		double housePrice = area * price;
		DetailResult result = new DetailResult();
		chargeDetail.add(getChargeDetailItem("所得税(卖方) " + sellerTaxRatio * 100
				+ "%", housePrice * sellerTaxRatio, result));

		chargeDetail.add(getChargeDetailItem("交易手续费(卖方) "
				+ sellerChargePerSquare + "/平方", sellerChargePerSquare * area,
				result));

		chargeDetail.add(getChargeDetailItem("所得税(买方) " + buyerTaxRatio * 100
				+ "%", housePrice * buyerTaxRatio, result));

		chargeDetail.add(getChargeDetailItem("交易手续费(买方) "
				+ buyerChargePerSquare + "/平方", buyerChargePerSquare * area,
				result));
		chargeDetail.add(getChargeDetailItem("交易登记费", tradeRegister, result));
		chargeDetail
				.add(getChargeDetailItem("抵押登记费", mortgageRegister, result));
		chargeDetail.add(getChargeDetailItem("配图费", pic, result));
		chargeDetail.add(getChargeDetailItem("完税印花", stampTax, result));
		return result.getTotal();
	}

	private HashMap<String, String> getChargeDetailItem(String name,
			double value, DetailResult result) {
		HashMap<String, String> item = new HashMap<String, String>();
		item.put("textViewName", name);
		item.put("textViewPrice", String.format("%.2f", value));
		result.appendPrice(value);
		return item;
	}

	class DetailResult {
		private double total;

		public DetailResult() {
			this(0d);
		}

		public DetailResult(double total) {
			this.total = total;
		}

		public double getTotal() {
			return total;
		}

		public void appendPrice(double price) {
			total += price;
		}
	}
}
