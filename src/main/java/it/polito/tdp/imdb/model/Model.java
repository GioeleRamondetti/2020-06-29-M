package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.imdb.db.Adjacency;
import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {

	private Graph<Director,DefaultWeightedEdge> grafo;
	private ImdbDAO dao=new ImdbDAO();
	private Map<Integer,Director> idmap;
	private int nvertici;
	private int narchi;
	private LinkedList<Director> best;
	private int distanzatot;
	
	public List<Director> getdirector(int y) {
		return dao.listAllDirectorsbyanno(y);
	}
	
	public void creagrafo(int y) {
		grafo=new SimpleWeightedGraph<Director,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		idmap=new HashMap<Integer,Director>();
		Graphs.addAllVertices(grafo, dao.listAllDirectorsbyanno(y));
		List<Director> l=new LinkedList<Director>(dao.listAllDirectorsbyanno(y));
		for(Director d:l) {
			idmap.put(d.getId(), d);
		}
		List<Adjacency> l1=new LinkedList<Adjacency>(dao.getAdjacencies( idmap));
		for(Adjacency d1:l1) {
				if(d1.getA1().getId()!=d1.getA2().getId()) {
						grafo.addEdge(d1.getA1(), d1.getA2());
						grafo.setEdgeWeight(d1.getA1(), d1.getA2(), d1.getWeight());
					}
				}
		this.nvertici=grafo.vertexSet().size();
		this.narchi=grafo.edgeSet().size();
	}
	
	public List<coppia> getvicini(Director d){
		List<Director> l=new LinkedList<Director>(Graphs.neighborListOf(grafo, d));
		List<coppia> c=new LinkedList<coppia>();
		for(Director d1:l) {
			c.add(new coppia(d,grafo.getEdgeWeight(grafo.getEdge(d, d1))));
		}
		Collections.sort(c,new comparatore());
		return c;
	}

	public int getNvertici() {
		return nvertici;
	}

	

	public int getNarchi() {
		return narchi;
	}

	public List<Director> getpercorsomax(int soglia){
		best=new LinkedList<>();
		
		List<Director> parziale =new LinkedList<>();
		for(Director d:idmap.values()) {
			
		parziale.add(d);
		cerca(parziale,soglia);
		parziale.clear();
		}
		return this.best;
	}
	private void cerca(List<Director> parziale,int soglia) {
		// condizione terminazione
		
			if(parziale.size()>best.size()) {
				best=new LinkedList<>(parziale);
			}
			
			
		
		// scorro i vicini dell'ultimo inserito ed esploro
		
		 for(Director v :Graphs.neighborListOf(grafo, parziale.get(parziale.size()-1))) {
			 if(!parziale.contains(v) && distanzatot<soglia) {
				 // evito cicli
				 parziale.add(v);
				 this.distanzatot= (int) (this.distanzatot+ grafo.getEdgeWeight(grafo.getEdge(v, parziale.get(parziale.size()-1))));
				 cerca(parziale, soglia);
				 parziale.remove(parziale.size()-1);
				 this.distanzatot= (int) (this.distanzatot- grafo.getEdgeWeight(grafo.getEdge(v, parziale.get(parziale.size()-1))));
				 }
		 }
		 
		
	}
}
