/**
 * A bloom filter implementation.
 * 
 * @author William Alexandre Fiset, william.alexandre.fiset@gmail.com
 **/

public class BloomFilter {
  
  // The number of bitsets. This should be proportional
  // to the number of hash functions for this bloom filter
  private final int N_SETS;

  // Doing '& 0x7F' is the same as modding by 64 but faster
  private final static long MOD64 = 0x7F; // equivalently: 0b1111111;

  // A 2D array containing the bitsets
  private final long[][] bitsets;

  // Tracks the size of the bitsets in this bloom filter
  private final int[] SET_SIZES;
  
  // Create a bloom filter with a various bitsets of different sizes
  public BloomFilter(int[] bitSetSizes) {
    SET_SIZES = bitSetSizes.clone();  
    N_SETS = bitSetSizes.length;
    bitsets = new long[N_SETS][];
    for (int i = 0; i < N_SETS; i++) {
      bitsets[i] = new long[SET_SIZES[i]];
    }
  }
  
  // Add a hash value to one of the bitsets in the bloom filter
  public void add(int setIndex, long hash) {
    hash = hash % SET_SIZES[setIndex];
    int block = (int)(hash / 64);
    bitsets[setIndex][block] |= (1L << (hash & MOD64));
  }

  // Adds a group of related hash values to the bloom filter.
  // These hash values should be the hash values that were applied
  // to all the various hash functions on the same key.
  public void add(long[] hashes) {
    for(int i = 0; i < N_SETS; i++) {
      add(i, hashes[i]);
    }
  }

  // Checks if a particular key is found within the bloom filter
  public boolean contains(long [] hashes) {
    for(int i = 0; i < hashes.length; i++) {
      int block = (int)(hashes[i] / 64);
      long MASK = 1L << (hashes[i] & MOD64);
      if ( (bitsets[i][block] & MASK) != MASK )
        return false;
    }
    return true;
  }

  @Override public String toString() {
    
    int maxSz = 0;
    for(int setSize : SET_SIZES) maxSz = Math.max(maxSz, setSize);

    char[][] matrix = new char[N_SETS][maxSz];
    for (char [] ar : matrix ) java.util.Arrays.fill(ar, '0');

    for (int k = 0; k < N_SETS; k++) {
      for (int i = 0; i < SET_SIZES[k]; i++) {
        int block = i / 64;
        int offset = i % 64;
        long mask = 1L << offset;
        if ( (bitsets[k][block] & mask) == mask ) {
          matrix[k][i] = '1';
        }
      }
    }

    StringBuilder sb = new StringBuilder();
    for (char[] row : M) sb.append(new String(row) + "\n");
    return sb.toString();

  }

}

