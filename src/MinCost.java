/**
 * 
 */

/**
 * @author https://sites.google.com/site/indy256/algo/min_cost_flow_pot
 * @runtime O(min(E^2*V*logV, E*logV*FLOW))
 *
 */
import java.util.*;

public class MinCost {

	/**
	 * @param args
	 */
	  static class Edge {
		    int to, f, cap, cost, rev;

		    Edge(int v, int cap, int cost, int rev) {
		      this.to = v;
		      this.cap = cap;
		      this.cost = cost;
		      this.rev = rev;
		    }
		  }

		  public static List<Edge>[] createGraph(int n) {
		    List<Edge>[] graph = new List[n];
		    for (int i = 0; i < n; i++)
		      graph[i] = new ArrayList<>();
		    return graph;
		  }

		  public static void addEdge(List<Edge>[] graph, int s, int t, int cap, int cost) {
		    graph[s].add(new Edge(t, cap, cost, graph[t].size()));
		    graph[t].add(new Edge(s, 0, -cost, graph[s].size() - 1));
		  }

		  static void bellmanFord(List<Edge>[] graph, int s, int[] dist) {
		    int n = graph.length;
		    Arrays.fill(dist, Integer.MAX_VALUE);
		    dist[s] = 0;
		    boolean[] inqueue = new boolean[n];
		    int[] q = new int[n];
		    int qt = 0;
		    q[qt++] = s;
		    for (int qh = 0; (qh - qt) % n != 0; qh++) {
		      int u = q[qh % n];
		      inqueue[u] = false;
		      for (int i = 0; i < graph[u].size(); i++) {
		        Edge e = graph[u].get(i);
		        if (e.cap <= e.f)
		          continue;
		        int v = e.to;
		        int ndist = dist[u] + e.cost;
		        if (dist[v] > ndist) {
		          dist[v] = ndist;
		          if (!inqueue[v]) {
		            inqueue[v] = true;
		            q[qt++ % n] = v;
		          }
		        }
		      }
		    }
		  }

		  public static int[] minCostFlow(List<Edge>[] graph, int s, int t, int maxf) {
		    int n = graph.length;
		    int[] prio = new int[n];
		    int[] curflow = new int[n];
		    int[] prevedge = new int[n];
		    int[] prevnode = new int[n];
		    int[] pot = new int[n];

		    // bellmanFord invocation can be skipped if edges costs are non-negative
		    bellmanFord(graph, s, pot);
		    int flow = 0;
		    int flowCost = 0;
		    while (flow < maxf) {
		      PriorityQueue<Long> q = new PriorityQueue<>();
		      q.add((long) s);
		      Arrays.fill(prio, Integer.MAX_VALUE);
		      prio[s] = 0;
		      boolean[] finished = new boolean[n];
		      curflow[s] = Integer.MAX_VALUE;
		      while (!finished[t] && !q.isEmpty()) {
		        long cur = q.remove();
		        int u = (int) (cur & 0xFFFF_FFFFL);
		        int priou = (int) (cur >>> 32);
		        if (priou != prio[u])
		          continue;
		        finished[u] = true;
		        for (int i = 0; i < graph[u].size(); i++) {
		          Edge e = graph[u].get(i);
		          if (e.f >= e.cap)
		            continue;
		          int v = e.to;
		          int nprio = prio[u] + e.cost + pot[u] - pot[v];
		          if (prio[v] > nprio) {
		            prio[v] = nprio;
		            q.add(((long) nprio << 32) + v);
		            prevnode[v] = u;
		            prevedge[v] = i;
		            curflow[v] = Math.min(curflow[u], e.cap - e.f);
		          }
		        }
		      }
		      if (prio[t] == Integer.MAX_VALUE)
		        break;
		      for (int i = 0; i < n; i++)
		        if (finished[i])
		          pot[i] += prio[i] - prio[t];
		      int df = Math.min(curflow[t], maxf - flow);
		      flow += df;
		      for (int v = t; v != s; v = prevnode[v]) {
		        Edge e = graph[prevnode[v]].get(prevedge[v]);
		        e.f += df;
		        graph[v].get(e.rev).f -= df;
		        flowCost += df * e.cost;
		      }
		    }
		    return new int[]{flow, flowCost};
		  }	
	
	
	
		// Usage example
	public static void main(String[] args) {
		// TODO �Զ����ɵķ������
	    List<Edge>[] graph = createGraph(3);
	    addEdge(graph, 0, 1, 3, 1);
	    addEdge(graph, 0, 2, 2, 1);
	    addEdge(graph, 1, 2, 2, 1);
	    int[] res = minCostFlow(graph, 0, 2, Integer.MAX_VALUE);
	    int flow = res[0];
	    int flowCost = res[1];
	    //�ж�������Ƿ�Ϊ4
	    System.out.println(4 == flow);
	    //��С�����Ƿ�Ϊ6
	    System.out.println(6 == flowCost);
	}

}