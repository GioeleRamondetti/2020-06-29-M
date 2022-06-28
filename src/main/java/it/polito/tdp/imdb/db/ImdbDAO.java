package it.polito.tdp.imdb.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;

import it.polito.tdp.imdb.model.Actor;
import it.polito.tdp.imdb.model.Director;
import it.polito.tdp.imdb.model.Movie;

public class ImdbDAO {
	
	public List<Actor> listAllActors(){
		String sql = "SELECT * FROM actors";
		List<Actor> result = new ArrayList<Actor>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Actor actor = new Actor(res.getInt("id"), res.getString("first_name"), res.getString("last_name"),
						res.getString("gender"));
				
				result.add(actor);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Movie> listAllMovies(){
		String sql = "SELECT * FROM movies";
		List<Movie> result = new ArrayList<Movie>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Movie movie = new Movie(res.getInt("id"), res.getString("name"), 
						res.getInt("year"), res.getDouble("rank"));
				
				result.add(movie);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public int count(int id1,int id2){
		String sql = "SELECT COUNT(a.id) as c FROM movies_directors md,directors d ,roles r, actors a "
				+ "WHERE md.director_id=d.id AND r.actor_id=a.id AND r.movie_id=md.movie_id AND d.id=? AND r.actor_id IN( "
				+ "SELECT a1.id  FROM movies_directors md1,directors d1 ,roles r1, actors a1   "
				+ "WHERE md1.director_id=d1.id AND r1.actor_id=a1.id AND r1.movie_id=md1.movie_id AND d1.id=?) "
				+ "GROUP BY a.id";
		int result =0;
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, id1);
			st.setInt(2, id2);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				result =result+res.getInt("c");
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public List<Director> listAllDirectorsbyanno(int y){
		String sql = "SELECT DISTINCT(d.id),d.first_name,d.last_name FROM directors d, movies_directors md,movies m WHERE m.year=? AND d.id=md.director_id\n"
				+ "AND m.id=md.movie_id";
		List<Director> result = new ArrayList<Director>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, y);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Director director = new Director(res.getInt("d.id"), res.getString("d.first_name"), res.getString("d.last_name"));
				
				result.add(director);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Director> listAllDirectors(){
		String sql = "SELECT * FROM directors";
		List<Director> result = new ArrayList<Director>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Director director = new Director(res.getInt("id"), res.getString("first_name"), res.getString("last_name"));
				
				result.add(director);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Adjacency> getAdjacencies(Map<Integer, Director> idmap){
		String sql = "SELECT d1.id as dd1, d2.id as dd2, COUNT(DISTINCT a.id) as weight "
				+ "FROM directors  d1,directors d2,movies_directors md1,actors a,roles r,movies_directors md2 "
				+ "WHERE d1.id<>d2.id AND d1.id=md2.director_id AND d2.id=md1.director_id AND a.id=r.actor_id "
				+ "AND r.movie_id=md1.movie_id AND md2.movie_id=md1.movie_id "
				+ "\n"
				+ "GROUP BY d1.id,d2.id";
		
		List<Adjacency> result = new ArrayList<Adjacency>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				if(idmap.containsKey(res.getInt("dd1")) && idmap.containsKey(res.getInt("dd2"))) {
					result.add(new Adjacency(idmap.get(res.getInt("dd1")),idmap.get(res.getInt("dd2")), res.getInt("weight")));
				}
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	
}
