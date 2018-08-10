package quaternary.incorporeal.flower;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import quaternary.incorporeal.etc.helper.DespacitoHelper;
import vazkii.botania.api.subtile.RadiusDescriptor;
import vazkii.botania.api.subtile.SubTileFunctional;

//based on real life "sweet alyssum" flower. This is so sad, Alexa play Despacito.
public class SubTileSweetAlexum extends SubTileFunctional {
	private int ticksPaused = 0;
	private int ticksSinceReset = 0;
	
	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		ticksSinceReset = (int) world.getTotalWorldTime();
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		if(getWorld().isRemote) return;
		
		if(redstoneSignal == 15) {
			resetTime();
		} else if(redstoneSignal > 0 || mana < 100) {
			ticksPaused++;
		} else {
			World world = getWorld();
			
			int tick = (int) (world.getTotalWorldTime() - ticksSinceReset - ticksPaused - 1);
			if(tick < 0 || tick % getTicksBetweenNotes() != 0) return;
			tick /= getTicksBetweenNotes();
			
			//Yeah this code is disgusting, but Chill, it's a meme flower
			
			BlockPos flutePos = null;
			BlockPos snarePos = null;
			BlockPos bassdrumPos = null;
			BlockPos bassguitarPos = null;
			BlockPos pos = getPos();
			
			int range = getRange();
			for(BlockPos bp : BlockPos.getAllInBoxMutable(pos.add(-range, 0, -range), pos.add(range, 1, range))) {
				if(world.getBlockState(bp).getBlock() == Blocks.NOTEBLOCK) {
					NoteBlockEvent.Instrument instUnder = DespacitoHelper.getInstrumentFromState(world.getBlockState(bp.down()));
					if(instUnder == NoteBlockEvent.Instrument.FLUTE && flutePos == null) {
						flutePos = bp.toImmutable();
						continue;
					}
					
					if(instUnder == NoteBlockEvent.Instrument.SNARE && snarePos == null) {
						snarePos = bp.toImmutable();
						continue;
					}
					
					if(instUnder == NoteBlockEvent.Instrument.BASSDRUM && bassdrumPos == null) {
						bassdrumPos = bp.toImmutable();
						continue;
					}
					
					if(instUnder == NoteBlockEvent.Instrument.BASSGUITAR && bassguitarPos == null) {
						bassguitarPos = bp.toImmutable();
						continue;
					}
				}
			}
			
			boolean dirtyMana = false;
			
			if(flutePos != null) {
				for(int note : DespacitoHelper.getNotesForTick(tick, NoteBlockEvent.Instrument.FLUTE)) {
					world.addBlockEvent(flutePos, Blocks.NOTEBLOCK, NoteBlockEvent.Instrument.FLUTE.ordinal(), note);
					mana -= 10;
					dirtyMana = true;
				}
			}
			
			if(snarePos != null) {
				for(int note : DespacitoHelper.getNotesForTick(tick, NoteBlockEvent.Instrument.SNARE)) {
					world.addBlockEvent(snarePos, Blocks.NOTEBLOCK, NoteBlockEvent.Instrument.SNARE.ordinal(), note);
					mana -= 10;
					dirtyMana = true;
				}
			}
			
			if(bassdrumPos != null) {
				for(int note : DespacitoHelper.getNotesForTick(tick, NoteBlockEvent.Instrument.BASSDRUM)) {
					world.addBlockEvent(bassdrumPos, Blocks.NOTEBLOCK, NoteBlockEvent.Instrument.BASSDRUM.ordinal(), note);
					mana -= 10;
					dirtyMana = true;
				}
			}
			
			if(bassguitarPos != null) {
				for(int note : DespacitoHelper.getNotesForTick(tick, NoteBlockEvent.Instrument.BASSGUITAR)) {
					world.addBlockEvent(bassguitarPos, Blocks.NOTEBLOCK, NoteBlockEvent.Instrument.BASSGUITAR.ordinal(), note);
					mana -= 10;
					dirtyMana = true;
				}
			}
			
			if(dirtyMana)	sync();
		}
	}
	
	@Override
	public boolean acceptsRedstone() {
		return true;
	}
	
	@Override
	public int getMaxMana() {
		return 2000;
	}
	
	@Override
	public int getColor() {
		return 0xBB4422;
	}
	
	private void resetTime() {
		ticksSinceReset = (int) getWorld().getTotalWorldTime();
		ticksPaused = 0;
	}
	
	@Override
	public void writeToPacketNBT(NBTTagCompound cmp) {
		cmp.setInteger("LastResetTicks", ticksSinceReset);
		cmp.setInteger("PausedTicks", ticksPaused);
		super.writeToPacketNBT(cmp);
	}
	
	@Override
	public void readFromPacketNBT(NBTTagCompound cmp) {
		super.readFromPacketNBT(cmp);
		ticksSinceReset = cmp.getInteger("LastResetTicks");
		ticksPaused = cmp.getInteger("PausedTicks");
	}
	
	protected int getRange() {
		return 4;
	}
	
	protected int getTicksBetweenNotes() {
		return 4;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public RadiusDescriptor getRadius() {
		return new RadiusDescriptor.Square(getPos(), getRange());
	}
	
	public static class Mini extends SubTileSweetAlexum {
		@Override
		protected int getRange() {
			return 1;
		}
		
		@Override
		protected int getTicksBetweenNotes() {
			return 3;
		}
	}
}