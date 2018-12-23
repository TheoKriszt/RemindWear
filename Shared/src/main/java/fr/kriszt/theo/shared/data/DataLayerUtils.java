package fr.kriszt.theo.shared.data;

import android.content.Context;
import android.support.annotation.WorkerThread;
import android.util.Log;

import java.util.Collection;
import java.util.HashSet;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by T.Kriszt on 08/10/2018.
 */
public class DataLayerUtils {

    private static final String TAG = "DataLayerUtils";

    @WorkerThread
    /**
     * @deprecated
     * See  DataLayerUtils
     */
    public static  Collection<String> getNodes(Context context) {
        HashSet<String> results = new HashSet<>();

        Task<List<Node>> nodeListTask =
                Wearable.getNodeClient(context).getConnectedNodes();

        try {
            // Block on a task and get the result synchronously (because this is on a background
            // thread).
            List<Node> nodes = Tasks.await(nodeListTask);

            for (Node node : nodes) {
                results.add(node.getId());
            }

        } catch (ExecutionException exception) {
            Log.e(TAG, "Task failed: " + exception);

        } catch (InterruptedException exception) {
            Log.e(TAG, "Interrupt occurred: " + exception);
        }

        return results;
    }
}
