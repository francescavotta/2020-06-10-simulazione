package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {
	
	Graph <Actor, DefaultWeightedEdge> grafo;
	Map <Integer, Actor> idMap; //mappa con tutti gli attori
	ImdbDAO dao = new ImdbDAO();
	List<Actor> vertici;
	
	public List<String> getGeneri(){
		return dao.listAllGenres();	
	}
	
	public String creaGrafo(String genere) {
		
		grafo = new SimpleWeightedGraph<Actor, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		idMap = new HashMap<>();
		for(Actor a: dao.listAllActors()) {
			idMap.put(a.getId(), a);
		}
		
		//aggiungo i vertici
		vertici = dao.getVertici(genere, idMap);
		Collections.sort(vertici);
		Graphs.addAllVertices(grafo, vertici);
		
		//aggiungo gli archi
		for(Arco aa: dao.getArchi(genere, idMap)) {
			if(grafo.containsEdge(aa.getA1(), aa.getA2()) || grafo.containsEdge(aa.getA1(), aa.getA2())) {
				//ricalcolo il peso
				DefaultWeightedEdge e = grafo.getEdge(aa.getA1(), aa.getA2()); 
				int peso = (int) grafo.getEdgeWeight(e);
				peso = peso + aa.getPeso();
				grafo.setEdgeWeight(e, peso);
			}else {
				//devo aggiungere l'arco
				Graphs.addEdgeWithVertices(grafo, aa.getA1(), aa.getA2(), aa.getPeso());
			}
		}
		
		return String.format("Grafo creato con %d vertici e %d archi \n", grafo.vertexSet().size(), grafo.edgeSet().size());
	}
	
	public List<Actor> getVertici(){
		if(grafo!= null)
			return vertici;
		return null;
	}
	
	public List<Actor> attoriSimili(Actor a){
		List<Actor> attori = new LinkedList<>();
		DepthFirstIterator<Actor, DefaultWeightedEdge> bfv = new DepthFirstIterator<>(this.grafo, a);
		
		while(bfv.hasNext()) {
			Actor f = bfv.next();
			attori.add(f);
		}
		attori.remove(a);
		Collections.sort(attori);
		return attori;
	}

}
