package quaternary.incorporeal.cygnus.types;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import quaternary.incorporeal.api.cygnus.ICygnusDatatype;
import quaternary.incorporeal.cygnus.CygnusDatatypeHelpers;
import quaternary.incorporeal.cygnus.CygnusStack;

import java.util.Optional;

public class CygnusStackType implements ICygnusDatatype<CygnusStack> {
	@Override
	public Class<CygnusStack> getTypeClass() {
		return CygnusStack.class;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt, CygnusStack item) {
		nbt.setTag("Stack", item.toNBT());
	}
	
	@Override
	public CygnusStack readFromNBT(NBTTagCompound nbt) {
		CygnusStack s = new CygnusStack(0);
		s.fromNBT(nbt.getCompoundTag("Stack"));
		return s;
	}
	
	@Override
	public void writeToPacketBuffer(PacketBuffer buf, CygnusStack item) {
		item.toPacketBuffer(buf);
	}
	
	@Override
	public CygnusStack readFromPacketBuffer(PacketBuffer buf) {
		CygnusStack s = new CygnusStack(0);
		s.fromPacketBuffer(buf);
		return s;
	}
	
	@Override
	public boolean areEqual(CygnusStack item1, CygnusStack item2) {
		if(item1.maxDepth() != item2.maxDepth()) {
			return false;
		} else if(item1.depth() != item2.depth()) {
			return false;
		} else {
			for(int i = 0; i < item1.depth(); i++) {
				Optional<Object> a = item1.peek(i);
				Optional<Object> b = item2.peek(i);
				
				if(a.isPresent() && b.isPresent()) {
					Object oa = a.get();
					Object ob = b.get();
					
					if(oa.getClass() != ob.getClass()) {
						return false;
					} else if(!CygnusDatatypeHelpers.forClass(oa.getClass()).areEqualUnchecked(oa, ob)) {
						return false;
					}
				} else if(a.isPresent() != b.isPresent()) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	@Override
	public String toString(CygnusStack item) {
		//lol
		StringBuilder b = new StringBuilder();
		b.append("Stack ").append(item.depth());
		for(int i = 0; i < item.depth(); i++) {
			b.append(' ');
			item.peek(i).ifPresent(b::append);
		}
		return b.toString();
	}
}