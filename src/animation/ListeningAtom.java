package animation;

import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.nonotify.NNAtom;

public class ListeningAtom implements IAtom {
    
    private ListeningAtomContainer parent;
    
    private NNAtom delegateAtom;

    public ListeningAtom() {
        delegateAtom = new NNAtom();
    }
    
    public ListeningAtom(String elementString) {
        delegateAtom = new NNAtom(elementString);
    }

    public ListeningAtom(IElement element) {
        delegateAtom = new NNAtom(element);
    }

    public ListeningAtom(String elementString, Point2d point) {
        delegateAtom = new NNAtom(elementString, point);
    }

    public ListeningAtom(String elementString, Point3d point) {
        delegateAtom = new NNAtom(elementString, point);
    }
    
    public void setParent(ListeningAtomContainer parent) {
        this.parent = parent; 
    }
    
    public Object clone() {
        return null;
    }

    @Override
    public void setPoint2d(Point2d point2d) {
        parent.postLowLevelEvent(new MicroEvent(this));
        delegateAtom.setPoint2d(point2d);
    }

    @Override
    public Point2d getPoint2d() {
        return delegateAtom.getPoint2d();
    }

    @Override
    public void setAtomTypeName(String identifier) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setMaxBondOrder(Order maxBondOrder) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setBondOrderSum(Double bondOrderSum) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getAtomTypeName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Order getMaxBondOrder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double getBondOrderSum() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setFormalCharge(Integer charge) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Integer getFormalCharge() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setFormalNeighbourCount(Integer count) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Integer getFormalNeighbourCount() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setHybridization(Hybridization hybridization) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Hybridization getHybridization() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setCovalentRadius(Double radius) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Double getCovalentRadius() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setValency(Integer valency) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Integer getValency() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setNaturalAbundance(Double naturalAbundance) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setExactMass(Double exactMass) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Double getNaturalAbundance() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double getExactMass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getMassNumber() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setMassNumber(Integer massNumber) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Integer getAtomicNumber() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setAtomicNumber(Integer atomicNumber) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getSymbol() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setSymbol(String symbol) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addListener(IChemObjectListener col) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getListenerCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void removeListener(IChemObjectListener col) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setNotification(boolean bool) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean getNotification() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void notifyChanged() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void notifyChanged(IChemObjectChangeEvent evt) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setProperty(Object description, Object property) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removeProperty(Object description) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Object getProperty(Object description) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<Object, Object> getProperties() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getID() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setID(String identifier) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setFlag(int flag_type, boolean flag_value) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean getFlag(int flag_type) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setProperties(Map<Object, Object> properties) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setFlags(boolean[] flagsNew) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean[] getFlags() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IChemObjectBuilder getBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setCharge(Double charge) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Double getCharge() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setImplicitHydrogenCount(Integer hydrogenCount) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Integer getImplicitHydrogenCount() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setPoint3d(Point3d point3d) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setFractionalPoint3d(Point3d point3d) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setStereoParity(Integer stereoParity) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Point3d getPoint3d() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Point3d getFractionalPoint3d() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getStereoParity() {
        // TODO Auto-generated method stub
        return null;
    }
   

}
