import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Stack;

public class MaxFlow {
    private static FastScanner in;

    public static void main(String[] args) throws IOException {
        in = new FastScanner();

        FlowGraph graph = readGraph();
        System.out.println(maxFlow(graph, 0, graph.size() - 1));
    }

    private static int maxFlow(FlowGraph graph, int from, int to) {
        int flow = 0;
        FlowPath fp = new FlowPath();
        while (fp!=null){
            while(!fp.edges.isEmpty()){
                Integer edgeNum = fp.edges.pop();
                graph.addFlow(edgeNum, fp.flow);
            }
            flow = flow + fp.flow;
            fp = getPath(graph);
        }
        return flow;
    }

    static FlowGraph readGraph() throws IOException {
        int vertex_count = in.nextInt();
        int edge_count = in.nextInt();
        FlowGraph graph = new FlowGraph(vertex_count);

        for (int i = 0; i < edge_count; ++i) {
            int from = in.nextInt() - 1, to = in.nextInt() - 1, capacity = in.nextInt();
            graph.addEdge(from, to, capacity);
        }
        return graph;
    }

    static class Edge {
        int from, to, capacity, flow;

        public Edge(int from, int to, int capacity) {
            this.from = from;
            this.to = to;
            this.capacity = capacity;
            this.flow = 0;
        }
    }

    private static FlowPath getPath(FlowGraph gr){
        FlowPath fp = new FlowPath();
        PriorityQueue<Integer> q = new PriorityQueue<>();
        int s = gr.size()-1;
        Integer[] prevEdge = new Integer[s + 1];
        q.add(0);
        while(!q.isEmpty()){
             for(int edgeNum:gr.graph[q.poll()])  {
                 Edge e = gr.getEdge(edgeNum);
                 int cap = e.capacity - e.flow;
                 int to = e.to;
                 fp = updateFlowPath(gr, fp, q, s, prevEdge, edgeNum, cap, to);
             } 
            
        }
        
        return fp;
    }

    private static FlowPath updateFlowPath(FlowGraph gr, FlowPath fp, PriorityQueue<Integer> q, int s, Integer[] prevEdge, int edgeNum, int cap, int to) {
        if(prevEdge[to] == null && cap > 0){
            q.add(to);
            prevEdge[to] = edgeNum;
            if(to == s){
                int backTrack = to;
                fp.flow=Integer.MAX_VALUE;
                do{
                    backTrack = getBackTrack(gr, fp, prevEdge[backTrack], backTrack);
                } while(backTrack > 0);
                return fp;
            }
        }
        return null;
    }

    private static int getBackTrack(FlowGraph gr, FlowPath fp, Integer peNum1, int backTrack) {
        Integer peNum = peNum1;
        Edge pe = gr.getEdge(peNum);
        int availFlow = pe.capacity - pe.flow;
        if(availFlow < fp.flow)
            fp.flow = availFlow;
        fp.edges.add(peNum);
        backTrack = pe.from;
        return backTrack;
    }


    static class FlowPath{
        Stack<Integer> edges;
        int flow = 0;
        public FlowPath(){
            edges = new Stack<>();
        }
    }
    
    /* This class implements a bit unusual scheme to store the graph edges, in order
     * to retrieve the backward edge for a given edge quickly. */
    static class FlowGraph {
        /* List of all - forward and backward - edges */
        private List<Edge> edges;

        /* These adjacency lists store only indices of edges from the edges list */
        private List<Integer>[] graph;

        public FlowGraph(int n) {
            this.graph = (ArrayList<Integer>[])new ArrayList[n];
            for (int i = 0; i < n; ++i)
                this.graph[i] = new ArrayList<>();
            this.edges = new ArrayList<>();
        }

        public void addEdge(int from, int to, int capacity) {
            /* Note that we first append a forward edge and then a backward edge,
             * so all forward edges are stored at even indices (starting from 0),
             * whereas backward edges are stored at odd indices. */
            Edge forwardEdge = new Edge(from, to, capacity);
            Edge backwardEdge = new Edge(to, from, 0);
            graph[from].add(edges.size());
            edges.add(forwardEdge);
            graph[to].add(edges.size());
            edges.add(backwardEdge);
        }

        public int size() {
            return graph.length;
        }

        public List<Integer> getIds(int from) {
            return graph[from];
        }

        public Edge getEdge(int id) {
            return edges.get(id);
        }

        public void addFlow(int id, int flow) {
            /* To get a backward edge for a true forward edge (i.e id is even), we should get id + 1
             * due to the described above scheme. On the other hand, when we have to get a "backward"
             * edge for a backward edge (i.e. get a forward edge for backward - id is odd), id - 1
             * should be taken.
             *
             * It turns out that id ^ 1 works for both cases. Think this through! */
            edges.get(id).flow += flow;
            edges.get(id ^ 1).flow -= flow;
        }
    }

    static class FastScanner {
        private BufferedReader reader;
        private StringTokenizer tokenizer;

        public FastScanner() {
            reader = new BufferedReader(new InputStreamReader(System.in));
            tokenizer = null;
        }

        public String next() throws IOException {
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                tokenizer = new StringTokenizer(reader.readLine());
            }
            return tokenizer.nextToken();
        }

        public int nextInt() throws IOException {
            return Integer.parseInt(next());
        }
    }
}
