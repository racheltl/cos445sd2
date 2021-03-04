/********************************************************************** */
/* Names: Nsomma Alilonu, Shir Kalati, Rachel Lee                       */
/********************************************************************** */
import java.util.List;
import java.util.PriorityQueue;

public class Party {

    // private long totalAlphaVoters;
    // private long totalBetaVoters;
    private boolean overwhelmingMajority; // true if we have 2/3s of the total votes
    private boolean majority; // true if we have more voters
    
    public static Party New(bool isBeta, int numDistricts, List<Block> blocks) {
        // get the total number of alpha voters and beta voters
        long totalAlpha = 0;
        long totalBeta = 0;
        for (Block block: blocks) {
            totalAlpha = totalAlpha + block.alpha();
            totalBeta = totalBeta + block.beta()
        }

        // compute if we have an overwhelming majority?
        if ((totalBeta / totalAlpha >= 2 && isBeta) || (totalAlpha / totalBeta >= 2 && !isBeta)) {
            overwhelmingMajority = true;
        }
        else {
            overwhelmingMajority = false;
        }

        // see if we have a natural advantage or not
        if ((totalAlpha > totalBeta && !isBeta) || (totalBeta > totalAlpha && isBeta)) {
            majority = true;
        }
        else {
            majority = false;
        }

    }

    // partitions remaining blocks into numDistricts, each with remaining.size() / numDistrictsRemaining
    public List<List<Block>> cut(int numDistrictsRemaining, List<Block> remaining) {
        // First find the ratio of voters voting for the active party to voters voting 
        // for the non-active party in all the remaining blocks

        // Next, if the ratio is 3:2 or above, then spread the voters for the active party out evenly between the districts

        // Pick blocks where the active party is winning
        // to do this we create a priority queue and put all the districts into it
        
        // use a for loop to iterate through all the blocks we have
        
            // take out the district that has the lowest number of our voters
            
            // put the block into that district
            
            // put the district back into the priority queue
        
        // at the end, get out the list of districts and return it 
        
        
        
        // If the ratio is 2:3 or below, put as many voters in the active party’s favor in one district as possible
        // Sort the list of blocks somehow so that the blocks with the most voters in our favor go into the same district
        // Do this until the district can hold no more blocks 
        // Move on to the next district and do the same thing
        // Repeat until there are no more blocks with the majority of voters in our favor
        
        // Fill the rest of the districts with the remaining blocks


    }

    public List<Block> choose(List<List<Block>> districts) {
        // go through the districts and see if there is a district
        // where the enemy is winning by a significant margin (2/3s)

        
        //choose if so if such a district does not exist, then choose 
        // our winning district that wins with the fewest voters on our side

    }
    
    // inform the active party of the choice made by the nonactive party. 
    // // i guess we aren’t going to do this for now. just focus on implementing choose and cut
    public void accept (List<Block> chosen) {
        
    }
}