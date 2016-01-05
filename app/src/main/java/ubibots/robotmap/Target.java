package ubibots.robotmap;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

public class Target {
    private String dest = null;

    public String getDest() {
        return dest;
    }

    public void requirePlace() {
        final EditText editText = new EditText(MapsActivity.context);
        AlertDialog.Builder dialog = new AlertDialog.Builder(MapsActivity.context);
        dialog.setTitle("请输入目的地");
        dialog.setView(editText);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dest = editText.getText().toString();
                Flag.requireFinish = true;
            }
        });
        dialog.setNegativeButton("取消", null);
        dialog.create().show();
    }
}
