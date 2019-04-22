package net.javainthebox.imageeffector;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorSpecies;

public class SoftFocusEffector {
    private final static VectorSpecies<Integer> SPECIES
	= VectorSpecies.ofPreferred(int.class);
    private final static int KERNEL_SIZE = SPECIES.bitSize()/32;
    private static final VectorMask<Integer> FIRST_TRUE_MASK;

    static {
	boolean[] firstTrueMask = new boolean[SPECIES.length()];
        firstTrueMask[0] = true;
        FIRST_TRUE_MASK = VectorMask.fromValues(SPECIES, firstTrueMask);
    }

    public static int[] softenLoop(int[] buffer, int width, int height) {

        for (int h = KERNEL_SIZE*2; h < height - KERNEL_SIZE*2; h++) {
            for (int w = KERNEL_SIZE*2; w < width - KERNEL_SIZE*2; w++) {
                
                int r = 0;
                int g = 0;
                int b = 0;
		
                for (int hh = -KERNEL_SIZE*2; hh <= KERNEL_SIZE*2; hh++) {
                    for (int ww = -KERNEL_SIZE*2; ww <= KERNEL_SIZE*2; ww++) {
                        r += (buffer[(h + hh)*width + (w + ww)] & 0x00FF0000) >> 16;
                        g += (buffer[(h + hh)*width + (w + ww)] & 0x0000FF00) >> 8;
                        b += buffer[(h + hh)*width + (w + ww)] & 0x000000FF;
                    }
                }

                r /= (KERNEL_SIZE * 4 + 1)*(KERNEL_SIZE * 4 + 1)*2;
                g /= (KERNEL_SIZE * 4 + 1)*(KERNEL_SIZE * 4 + 1)*2;
                b /= (KERNEL_SIZE * 4 + 1)*(KERNEL_SIZE * 4 + 1)*2;

                r = (((buffer[h*width + w] & 0x00FF0000) >> 1) & 0x00FF0000)
		    + ((r<<16) & 0x00FF0000);
                r = r > 0x00FF0000 ? 0x00FF0000: r;    
                g = (((buffer[h*width + w] & 0x0000FF00) >> 1) & 0x0000FF00)
		    +  ((g<<8) & 0x0000FF00);
                g = g > 0x0000FF00 ? 0x0000FF00: g;    
                b = (((buffer[h*width + w] & 0x000000FF) >> 1) & 0x000000FF)
		    +  (b & 0x000000FF);
                b = b > 0x000000FF ? 0x000000FF: b;    
                
                buffer[h*width + w] = 0xFF000000 + r + g + b; 
            }
        }

        return buffer;
    }

    public static int[] softenVector(int[] buffer, int width, int height) {
	for (int h = KERNEL_SIZE*2; h < height - KERNEL_SIZE*2; h++) {
            for (int w = KERNEL_SIZE*2; w < width - KERNEL_SIZE*2; w++) {
                
		var vr = IntVector.zero(SPECIES);
                var vg = IntVector.zero(SPECIES);
                var vb = IntVector.zero(SPECIES);

                for (int hh = -KERNEL_SIZE*2 ; hh <= KERNEL_SIZE*2; hh++) {
		    var vector = IntVector.fromArray(SPECIES, buffer,
				   (h + hh)*width + (w - KERNEL_SIZE*2));
                    vr = vr.add(vector.and(0x00FF0000).shiftR(16));
                    vg = vg.add(vector.and(0x0000FF00).shiftR(8));
                    vb = vb.add(vector.and(0x000000FF));

                    vector = IntVector.fromArray(SPECIES, buffer,
                                   (h + hh)*width + (w - KERNEL_SIZE));
                    vr = vr.add(vector.and(0x00FF0000).shiftR(16));
                    vg = vg.add(vector.and(0x0000FF00).shiftR(8));
                    vb = vb.add(vector.and(0x000000FF));

                    vr = vr.add((buffer[(h+hh)*width + w] & 0x00FF0000) >> 16,
				FIRST_TRUE_MASK);
                    vg = vg.add((buffer[(h+hh)*width + w] & 0x0000FF00) >> 8,
				FIRST_TRUE_MASK);
                    vb = vb.add(buffer[(h+hh)*width + w] & 0x000000FF,
				FIRST_TRUE_MASK);

                    vector = IntVector.fromArray(SPECIES, buffer,
                                                 (h + hh)*width + (w + 1));
                    vr = vr.add(vector.and(0x00FF0000).shiftR(16));
                    vg = vg.add(vector.and(0x0000FF00).shiftR(8));
                    vb = vb.add(vector.and(0x000000FF));

                    vector = IntVector.fromArray(SPECIES, buffer,
                                                 (h + hh)*width + (w + 1 + KERNEL_SIZE));
                    vr = vr.add(vector.and(0x00FF0000).shiftR(16));
                    vg = vg.add(vector.and(0x0000FF00).shiftR(8));
                    vb = vb.add(vector.and(0x000000FF));
		}

		int r = vr.addAll();
                int g = vg.addAll();
                int b = vb.addAll();
		
                r /= (KERNEL_SIZE * 4 + 1)*(KERNEL_SIZE * 4 + 1)*2;
                g /= (KERNEL_SIZE * 4 + 1)*(KERNEL_SIZE * 4 + 1)*2;
                b /= (KERNEL_SIZE * 4 + 1)*(KERNEL_SIZE * 4 + 1)*2;

                r = (((buffer[h*width + w] & 0x00FF0000) >> 1) & 0x00FF0000)
		    + ((r<<16) & 0x00FF0000);
                r = r > 0x00FF0000 ? 0x00FF0000: r;    
                g = (((buffer[h*width + w] & 0x0000FF00) >> 1) & 0x0000FF00)
		    +  ((g<<8) & 0x0000FF00);
                g = g > 0x0000FF00 ? 0x0000FF00: g;    
                b = (((buffer[h*width + w] & 0x000000FF) >> 1) & 0x000000FF)
		    +  (b & 0x000000FF);
                b = b > 0x000000FF ? 0x000000FF: b;    
                
                buffer[h*width + w] = 0xFF000000 + r + g + b; 
            }
        }

        return buffer;
    }
}	
