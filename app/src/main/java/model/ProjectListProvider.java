package model;

import java.util.ArrayList;

public class ProjectListProvider {

    public ProjectListProvider(){

    }

    // TODO: get projects from restful API
    public ArrayList<String> getProjectList(){
        ArrayList<String> projectItems = new ArrayList<>();
        projectItems.add("PROJECT1");
        projectItems.add("PROJECT2");
        projectItems.add("PROJECT3");
        projectItems.add("PROJECT4");

        return projectItems;

    }

}
