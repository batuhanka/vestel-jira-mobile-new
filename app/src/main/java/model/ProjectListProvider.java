package model;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by batuhanka on 18.03.2016.
 */
public class ProjectListProvider {


    public ProjectListProvider(Context context){

    }

    // TODO: get projects from restful API
    public ArrayList<String> getProjectList(){
        ArrayList<String> projectItems				= new ArrayList<String>();
        projectItems.add("PROJECT1");
        projectItems.add("PROJECT2");
        projectItems.add("PROJECT3");
        projectItems.add("PROJECT4");

        return projectItems;

    }

}
