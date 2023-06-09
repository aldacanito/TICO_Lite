package IDC.Metrics;

import java.util.ArrayList;
import java.util.List;

public class ExecutionHistory
{
    String classURI;
    String propertyURI;
    String range;

    public static List<ExecutionHistory> ExecutionHistoryList =  new ArrayList<>();

    public static boolean inList(ExecutionHistory eh)
    {
        for(ExecutionHistory b : ExecutionHistory.ExecutionHistoryList)
        {
            if(b.range == eh.range && b.propertyURI == eh.propertyURI && b.classURI == eh.classURI)
                return true;
        }

        return false;
    }

    public static ExecutionHistory getExecutionHistory(String classURI, String propertyURI, String range)
    {
        for(ExecutionHistory b : ExecutionHistory.ExecutionHistoryList)
        {
            if(b.range == range && b.propertyURI == propertyURI && b.classURI == classURI)
                return b;
        }

        return null;
    }

    public static void addExecutionHistory(ExecutionHistory hg)
    {
        ExecutionHistory.ExecutionHistoryList.add(hg);
    }


    public static ExecutionHistory addExecutionHistory(String classURI, String propertyURI, String range)
    {
        ExecutionHistory hg = ExecutionHistory.getExecutionHistory(classURI, propertyURI, range);

        if(hg==null)
        {
            hg = new ExecutionHistory(classURI, propertyURI, range);
            ExecutionHistory.ExecutionHistoryList.add(hg);
        }

        return hg;
    }

    public ExecutionHistory(String classURI, String propertyURI, String range)
    {
        this.classURI    = classURI;
        this.propertyURI = propertyURI;
        this.range       = range;

    }

}
