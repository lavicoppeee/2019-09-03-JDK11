package it.polito.tdp.food.model;

import java.util.*;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.food.db.FoodDao;

public class Model {
	
	private FoodDao dao;
	
	private Graph<String,DefaultWeightedEdge> graph;
	private List<String> porzioni;

	private Double bestPeso;
	private List<String> bestCammino;
	
	public Model() {
		dao=new FoodDao();
	}
	
	public void creaGrafo(int calorie) {
		this.graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		porzioni = this.dao.getVertici(calorie);

		// aggiungi vertici
		Graphs.addAllVertices(this.graph, porzioni);

		List<Arco> archi = dao.getArchi();
		// aggiungi archi
		for (Arco a : archi) {
			if (this.graph.vertexSet().contains(a.getP1()) && this.graph.vertexSet().contains(a.getP2())) {
				Graphs.addEdgeWithVertices(graph, a.getP1(), a.getP2(), a.getPeso());
			}
		}

	}
	
	public List<String> Vertici() {
		return porzioni;
	}
	public Integer nArchi() {
		return graph.edgeSet().size();
	}
	
	public Integer nVertici() {
		return porzioni.size();
	}
	
	public List<NearPortion> getPorzioniCorrelate(String porzione){
		List<NearPortion> result=new ArrayList<>();
		
		List<String> vicini=Graphs.neighborListOf(graph, porzione);
		
		for(String v: vicini) {
			double peso=this.graph.getEdgeWeight(this.graph.getEdge(porzione, v));
			result.add(new NearPortion(v,peso));
		}
		
		return result;
	}
	
	
	
	public void cammino(int n, String porzione) {
		
		this.bestPeso=0.0;
		this.bestCammino=null;
		
        List<String> parziale=new ArrayList<>();
		parziale.add(porzione);
		
		ricorsione(parziale,n,1);
		
	}

	private void ricorsione(List<String> parziale, int n, int l) {
		// TODO Auto-generated method stub
		
		//caso terminale 
		//quando il livello raggiunge lo stesso n+1
		//aggiorno bestcammino e bestpeso
		if(l==n+1) {
			double peso=calcolaPeso(parziale);
			if(peso>bestPeso) {
			this.bestPeso=peso;
			this.bestCammino=new ArrayList<>(parziale);
			}
			return;
		}
		//ricorsione
		List<String> vicini=Graphs.neighborListOf(graph, parziale.get(l-1));
		for(String v:vicini) {
			if(!parziale.contains(v)){
				parziale.add(v);
				ricorsione(parziale,n,l+1);
				parziale.remove(v);
			}
		}
	}

	private double calcolaPeso(List<String> parziale) {
		double peso=0.0;
		
		for(int i=1; i<parziale.size();i++) {
			Double pNew=this.graph.getEdgeWeight(this.graph.getEdge(parziale.get(i-1),parziale.get(i)));
			peso+=pNew;
		}
		
		return peso;
	}
	
	public Double getBestPeso() {
		return bestPeso;
	}

	public List<String> getBestCammino() {
		return bestCammino;
	}
}
