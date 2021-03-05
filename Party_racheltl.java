/********************************************************************** */
/* Names: Nsomma Alilonu, Shir Kalati, Rachel Lee                       */
/********************************************************************** */
import java.util.List;
// import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.Collections;

public class Party_racheltl implements Party {

    // private long totalAlphaVoters;
    // private long totalBetaVoters;
    private boolean overwhelmingMajority; // true if we have 2/3s of the total votes
    private boolean majority; // true if we have more voters
    private boolean isBeta;
    
    private Party_racheltl(boolean isBeta, boolean overwhelmingMajority, boolean majority){
        this.isBeta = isBeta;
        this.overwhelmingMajority = overwhelmingMajority;
        this.majority = majority; 
    }
    
    public static Party New(boolean isBeta, int numDistricts, List<Block> blocks) {

        // System.out.println("We just entered New");
        // get the total number of alpha voters and beta voters
        long totalAlpha = 0;
        long totalBeta = 0;
        boolean overMaj;
        boolean maj;

        for (Block block: blocks) {
            totalAlpha = totalAlpha + block.alpha();
            totalBeta = totalBeta + block.beta();
        }

        // Make sure that totalAlpha and totalBeta are not 0 to avoid division complications.
        if (totalAlpha == 0) totalAlpha = 1;
        if (totalBeta == 0) totalBeta = 1;

        // compute if we have an overwhelming majority?
        if (((totalBeta / totalAlpha) >= 1 && isBeta) || ((totalAlpha / totalBeta) >= 1 && !isBeta)) {
            overMaj = true;
        }
        else {
            overMaj = false;
        }

        // see if we have a natural advantage or not
        if ((totalAlpha > totalBeta && !isBeta) || (totalBeta > totalAlpha && isBeta)) {
            maj = true;
        }
        else {
            maj = false;
        }
        return new Party_racheltl(isBeta, overMaj, maj);
    }

    // partitions remaining blocks into numDistricts, each with remaining.size() / numDistrictsRemaining
    public List<List<Block>> cut(int numDistrictsRemaining, List<Block> remaining) {
        // Make a list of the blocks sorted by how much we are winning compared to our opponent. (We overwhelmingly win --> Opponent overwhelmingly wins)
        remaining = new ArrayList<Block>(remaining); // get mutability
        
        Collections.sort(remaining, new Block.BlockComparator(false, true, isBeta)); // Sorted from most to least partisan

        // Initialize the list of districts.
        List<List<Block>> districts = new ArrayList<List<Block>>();
        for (int i = 0; i < numDistrictsRemaining; ++i) {
            List<Block> district = new ArrayList<Block>();
            districts.add(district);
        }
        
        int districtSize = remaining.size() / numDistrictsRemaining;

        // If we are winning by an overwhelming majority (2:1 or greater), spread the blocks where we are winning more or less evenly between the districts.
        if (overwhelmingMajority) {  
            // Go through each district, adding one block, then go back and add another, until you have no more blocks left.
            // int districtNum = 0;
            // for (Block block: remaining) {        
            //     districts.get(districtNum).add(block);
            //     districtNum = (districtNum+1) % numDistrictsRemaining;
            // }
            
            long[] betaSwings = new long[numDistrictsRemaining];

            for (Block block : remaining) {
              int extremum = -1;
              for (int i = 0; i < numDistrictsRemaining; ++i) {
                List<Block> district = districts.get(i);
                if (district.size() == districtSize) continue;
                if (extremum == -1) extremum = i;
                // we swing beta and this is the most alpha blockyet
                if (block.betaSwing() > 0 && betaSwings[i] < betaSwings[extremum]) extremum = i;
                // we swing alpha and this is the most beta block yet
                if (block.betaSwing() < 0 && betaSwings[i] > betaSwings[extremum]) extremum = i;
                // if we hit zero swing blocks then it doesn't matter anyhow
              }
              districts.get(extremum).add(block);
              // pulls down the swing on the beta swing
              betaSwings[extremum] += block.betaSwing();
            }
        }

        // If the ratio is less than 2:1, put as many voters in the active party’s favor in one district as possible
        else {
            int districtNum = 0;
            for (Block block: remaining) {
                // Make sure we only add to districts that are not full.
                if (districts.get(districtNum).size() == districtSize) {
                    districtNum++;
                }
                
                // Add blocks where we are winning to the district until the district is full, then go to the next one.
                districts.get(districtNum).add(block);   
            }
        }

        return districts;
    }

    // an important thing to note for this method is that we are choosing to treat
    // blocks as integer blocks once their voting allegiance is determined by a strict
    // majority (i.e., >50%). 
    public List<Block> choose(List<List<Block>> districts) {
        // System.out.println("We just entered choose!");
        // go through the districts and see if there is a district
        // where the enemy is winning by a significant margin (2/3s)
        List<Block> mostOppBlocksChamp = null; // CASE 1
        List<Block> mostOppBlocksChampBackup = null; // CASE 2
        List<Block> weWinButWithLeastUsBlocksChamp = districts.get(0); // CASE 3
        // mostOppBlocksChampBackup should be initialized to something so we don't
        // accidentally return null?
        
        long oppBlocksChamp = -1;
        long ourBlocksChamp = Long.MAX_VALUE;
        
        // iterate through districts
        for (List<Block> district: districts) {
            long totalBlocks = district.size(); // number of blocks in district
            
            // iterate through blocks for this district
            
            long oppBlocks = 0; // number of blocks that opp wins in this district
            long ourBlocks = 0; // number of blocks that WE win in this district

            for (Block block : district) {
                long oppVoters = isBeta ? block.alpha() : block.beta();
                long totalVoters = block.numVoters();
                // does opp win for this block? (strict win or lose)
                if (((double)oppVoters / (double)totalVoters) > 1.0/2.0) {
                    oppBlocks++;
                }
                else { // i.e. WE win that block
                    ourBlocks++;
                }
            }
            // we've now gone through every block in this district

            // CASE 1
            // check if enemy is winning by a significant margin in this district
            if (((double)oppBlocks / (double)totalBlocks) > 2.0/3.0) { // i.e., enemy wins by significant margin
                // then we want that one!
                // But no, we want to find the district where opp is winning by the MOST blocks
                if (oppBlocks > oppBlocksChamp) {
                    mostOppBlocksChamp = district;
                    oppBlocksChamp = oppBlocks;
                }
            }
            
            // CASE 2
            if (oppBlocks > ourBlocks) { // i.e. opp wins this district (could be by sig amount, maybe not)
                if (oppBlocks > oppBlocksChamp) {
                    mostOppBlocksChampBackup = district;
                    oppBlocksChamp = oppBlocks;
                }
            }

            // CASE 3
            if (ourBlocks > oppBlocks) { // i.e., we win this district
                // we want that one! (if we couldn't find a district
                // with enemy winning by a significant margin)
                // But no, we want to find the district where we are winning by the least blocks
                if (ourBlocks < ourBlocksChamp) {
                    weWinButWithLeastUsBlocksChamp = district;
                    ourBlocksChamp = ourBlocks;
                }
            }
            
        }
          
        // CASE 1
        if (mostOppBlocksChamp != null) { return mostOppBlocksChamp; }

        // let's return the district where the opposition wins, and the district
        // where they win by the most
        // CASE 2
        else if (mostOppBlocksChampBackup != null) { return mostOppBlocksChampBackup; }
 
        // our winning district that wins with the fewest voters on our side
        // CASE 3
        return weWinButWithLeastUsBlocksChamp; 

    }
    
    // inform the active party of the choice made by the nonactive party. 
    // // i guess we aren’t going to do this for now. just focus on implementing choose and cut
    public void accept (List<Block> chosen) {
        
    }
}