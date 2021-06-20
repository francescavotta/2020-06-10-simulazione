package it.polito.tdp.imdb.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {
	
	Graph <Actor, DefaultWeightedEdge> grafo;
	Map <Integer, Actor> idMap; //mappa con tutti gli attori
	ImdbDAO dao = new ImdbDAO();
	
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
		Graphs.addAllVertices(grafo, dao.getVertici(genere, idMap));
		
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
		
		return String.format("Grafo creato con %d vertici e %d archi", grafo.vertexSet().size(), grafo.edgeSet().size());
	}

}
