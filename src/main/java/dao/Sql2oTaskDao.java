package dao;

import models.Task;
import org.sql2o.*;
import java.util.List;

public class Sql2oTaskDao implements TaskDao{// implementing our interface
    private final Sql2o sql2o;
    public Sql2oTaskDao(Sql2o sql2o){
        this.sql2o = sql2o; //making the sql2o object available everywhere so we can call methods in it
    }

    @Override
    public void add(Task task) {
        String sql = "INSERT INTO tasks (description) VALUES (:description)"; //raw sql
        try (Connection con = sql2o.open()) { //try to open a connection
            int id = (int) con.createQuery(sql, true) //make a new variable
                    .bind(task) //map my argument onto the query so we can use information from it
                    .executeUpdate() //run it all
                    .getKey(); //int id is now the row number (row “key”) of db
            task.setId(id); //update object to set id now from database
        } catch (Sql2oException ex) {
            System.out.println(ex); //oops we have an error!
        }
    }

    @Override
    public List<Task> getAll() {
        try(Connection con = sql2o.open()){
            return con.createQuery("SELECT * FROM tasks") //raw sql
                    .executeAndFetch(Task.class); //fetch a list
        }
    }

    @Override
    public void update(int id, String newDescription){
        String sql = "UPDATE tasks SET description = :description WHERE id=:id";
        try(Connection con = sql2o.open()){
            con.createQuery(sql)
                    .addParameter("description", newDescription)// add parameter and the new updated description and id to the database.
                    .addParameter("id", id)
                    .executeUpdate();
        } catch (Sql2oException ex) {
            System.out.println(ex);
        }
    }


    @Override
    public Task findById(int id) {
        try(Connection con = sql2o.open()){//opens connection with the database.
            return con.createQuery("SELECT * FROM tasks WHERE id = :id")
                    .addParameter("id", id) //key/value pair, key must match above
                    // this passes the id argument to the SQL query.
                    .executeAndFetchFirst(Task.class); //fetch an individual item. returns the first item in the collection returned by the databases

        }
    }
    @Override
    public void deleteById(int id) {
        String sql = "DELETE from tasks WHERE id=:id";
        try (Connection con = sql2o.open()) {
            con.createQuery(sql)
                    .addParameter("id", id)
                    .executeUpdate();
        } catch (Sql2oException ex){
            System.out.println(ex);
        }
    }

    @Override
    public void clearAllTasks() {
        String sql = "DELETE from tasks";
        try (Connection con = sql2o.open()) {
            con.createQuery(sql)
                    .executeUpdate();
        } catch (Sql2oException ex){
            System.out.println(ex);
        }
    }
}
