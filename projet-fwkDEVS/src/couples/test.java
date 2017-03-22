
package couples;

import DEVSModel.DEVSCoupled;
import DEVSModel.Port;
import models.And;

public class test
    extends DEVSCoupled
{

    private And And0;
    private And And1;
    public Port in0;
    public Port in1;
    public Port in2;
    public Port out0;

    public test() {
        And0 = new And("And0");
        this.getSubModels().add(And0);
        And1 = new And("And1");
        this.getSubModels().add(And1);
        this.addIC(And1 .getOutPorts().get(0), And0 .getInPorts().get(0));
        in0 = new Port(this, "in0");
        this.addInPort(in0);
        this.addEIC(getInPort("in0"), And1 .getInPorts().get(0));
        in1 = new Port(this, "in1");
        this.addInPort(in1);
        this.addEIC(getInPort("in1"), And1 .getInPorts().get(1));
        in2 = new Port(this, "in2");
        this.addInPort(in2);
        this.addEIC(getInPort("in2"), And0 .getInPorts().get(1));
        out0 = new Port(this, "out0");
        this.addOutPort(out0);
        this.addEOC(And0 .getOutPorts().get(0), getOutPort("out0"));
    }

    @Override
    public void setSelectPriority() {
        return ;
    }

}
