package comp2450.logic;

import com.google.common.base.Preconditions;
import comp2450.model.ActivityRecord;
import comp2450.model.Profile;
import comp2450.model.World;

import java.util.*;

/** Builds a combined feed: user’s own activities + those of followed people (sorted newest first). */
public class FeedService {

    public static ArrayList<ActivityRecord> feedFor(Profile me, World world) {
        Preconditions.checkNotNull(me, "me");
        Preconditions.checkNotNull(world, "world");

        ArrayList<ActivityRecord> out = new ArrayList<>(me.activities());
        Set<UUID> following = me.followingIds();

        for (UUID id : following) {
            world.getProfile(id).ifPresent(p -> out.addAll(p.activities()));
        }

        out.sort((a,b) -> b.timestamp().compareTo(a.timestamp()));
        return out;
    }
}

