package com.webappid.mysql;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {

    private Connection connection = null;
    private String host, user, pass, db, port, url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = "root";
        pass = "";
        port = "3306";
        db = "nama_db";
        host = "192.168.0.1";

        url  = "jdbc:mysql://"
                + host
                + ":" + port
                + "/" + db + "?zeroDateTimeBehavior=convertToNull";


        new MysqlConn().execute();

    }

    private class MysqlConn extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... strings) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection(url, user, pass);
                String sql = "SELECT * FROM table_name ";

                String[][] result = open(sql, null);
                for(int i=0; i<result.length; i++){
                    String[] row = result[i];
                    for(int j=0; j<row.length;j++){
                        if(row[j]!=null) {
                            Log.d("Column Data", row[j]);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private String[][] open(String sql, String[] mVar) {
        PreparedStatement s = null;
        try {
            Log.d("Mysql Sql", sql);
            s = connection.prepareStatement(sql);
            int number = 1;

            if (mVar != null) {
                for (int i = 0; i < mVar.length; i++) {
                    s.setString(i + 1, mVar[i]);
                    number++;
                }
            }

            ResultSet result = s.executeQuery();
            String[][] strResult = null;

            ResultSetMetaData rsmd = result.getMetaData();

            int numberOfColumns = rsmd.getColumnCount();
            result.last();


            int rowCount = result.getRow();
            strResult = new String[rowCount][result.getMetaData().getColumnCount()];

            int j = 0;
            result.beforeFirst();

            while (result.next()) { // process results one row at a time

                for (int i = 1; i <= numberOfColumns; i++) {
                    strResult[j][i - 1] = result.getString(i);
                }

                j++;
            }

            sql = null;
            s = null;
            result = null;
            mVar = null;

            return strResult;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
