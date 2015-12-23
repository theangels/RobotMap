package ubibots.robotmap;

import android.support.v7.app.AlertDialog;
import android.widget.EditText;

/**
 * Created by TheAngels on 2015/12/23.
 */
public class Control {
    public void requirePlace() {
        new AlertDialog.Builder(MapsActivity.mContext).setTitle("请输入").setIcon(
                android.R.drawable.ic_dialog_info).setView(
                new EditText(MapsActivity.mContext)).setPositiveButton("确定", null)
        .setNegativeButton("取消", null).show();
    }
}
