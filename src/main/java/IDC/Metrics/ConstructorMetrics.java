package IDC.Metrics;

public class ConstructorMetrics
{


    private String constructorName;
    private String property_URI;
    private int against;
    private int support;
    private int neutral;


    public ConstructorMetrics(String propertyURI, String constructor)
    {
        this.property_URI = propertyURI;
        this.constructorName = constructor;
        this.against = 0;
        this.support = 0;
        this.neutral = 0;
    }

    public void clean()
    {
        this.against = 0;
        this.support = 0;
        this.neutral = 0;
    }

    public String toString()
    {
        String ret = "Computed results for constructor " + constructorName + " of " + property_URI + "\n";

        ret += "\n\t Evidence to Support: " + this.getSupport();

        ret += "\n\t Evidence Against: " + this.getAgainst();

        ret += "\n\t Neutral Instances of same Class: " + this.getNeutral();

        return ret;
    }

    public void addAgainst()
    {
        this.against = this.getAgainst() + 1;
    }

    public void addSupport()
    {
        this.support = this.getSupport() + 1;
    }

    public void addNeutral()
    {
        this.neutral = this.getNeutral() + 1;
    }


    public String getConstructorName() {
        return constructorName;
    }

    public String getProperty_URI() {
        return property_URI;
    }

    public int getAgainst() {
        return against;
    }

    public int getSupport() {
        return support;
    }

    public int getNeutral() {
        return neutral;
    }
}
