
package couples;

import DEVSModel.DEVSCoupled;
import gate.Generator;
import gate.Transducer;
import models.Not;

public class final1
    extends DEVSCoupled
{

    private Not Not0;
    public Generator Gen0;
    public Transducer Trans0;
    private test test0;

    public final1() {
        Not0 = new Not("Not0");
        this.getSubModels().add(Not0);
        Gen0 = new Generator("Gen0");
        this.getSubModels().add(Gen0);
        Trans0 = new Transducer("Trans0");
        this.getSubModels().add(Trans0);
        test0 = new test();
        this.getSubModels().add(test0);
        this.addIC(Gen0 .getOutPorts().get(0), Not0 .getInPorts().get(0));
        this.addIC(Gen0 .getOutPorts().get(0), test0 .getInPorts().get(1));
        this.addIC(Gen0 .getOutPorts().get(0), test0 .getInPorts().get(2));
        this.addIC(Not0 .getOutPorts().get(0), test0 .getInPorts().get(0));
        this.addIC(test0 .getOutPorts().get(0), Trans0 .getInPorts().get(0));
    }

    @Override
    public void setSelectPriority() {
        return ;
    }

}
