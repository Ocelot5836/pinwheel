import gg.moonflower.pinwheel.api.GeometryModelParser;
import gg.moonflower.pinwheel.api.geometry.bone.AnimatedBone;
import gg.moonflower.pinwheel.api.geometry.GeometryCompileException;
import gg.moonflower.pinwheel.api.geometry.GeometryModelData;
import gg.moonflower.pinwheel.api.geometry.GeometryTree;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

public class GeometryTreeTest {

    @Test
    public void testGeometryTree() throws GeometryCompileException {
        GeometryModelData model = GeometryModelParser.parseModel("{\"format_version\":\"1.12.0\",\"minecraft:geometry\":[{\"description\":{\"identifier\":\"creepie\",\"texture_width\":32,\"texture_height\":32,\"visible_bounds_width\":2,\"visible_bounds_height\":2.5,\"visible_bounds_offset\":[0,0.75,0]},\"bones\":[{\"name\":\"root\",\"pivot\":[0,0,0]},{\"name\":\"creepie\",\"parent\":\"root\",\"pivot\":[0,0,0],\"rotation\":[0,0,0]},{\"name\":\"r_leg_front\",\"parent\":\"creepie\",\"pivot\":[-2,3,-2],\"cubes\":[{\"origin\":[-3,0,-3],\"size\":[2,3,2],\"uv\":[16,12]}]},{\"name\":\"l_leg_front\",\"parent\":\"creepie\",\"pivot\":[2,3,-2],\"cubes\":[{\"origin\":[1,0,-3],\"size\":[2,3,2],\"uv\":[16,12]}]},{\"name\":\"r_leg_back\",\"parent\":\"creepie\",\"pivot\":[-2,3,2],\"cubes\":[{\"origin\":[-3,0,1],\"size\":[2,3,2],\"uv\":[16,17]}]},{\"name\":\"l_leg_back\",\"parent\":\"creepie\",\"pivot\":[2,3,2],\"cubes\":[{\"origin\":[1,0,1],\"size\":[2,3,2],\"uv\":[16,17]}]},{\"name\":\"upper_body\",\"parent\":\"creepie\",\"pivot\":[0,3,0],\"cubes\":[{\"origin\":[-2,2,-2],\"size\":[4,6,4],\"uv\":[0,12]}]},{\"name\":\"head\",\"parent\":\"upper_body\",\"pivot\":[0,8,0],\"cubes\":[{\"origin\":[-3,8,-3],\"size\":[6,6,6],\"uv\":[0,0]}]},{\"name\":\"floof\",\"parent\":\"head\",\"pivot\":[0,14,0],\"cubes\":[{\"origin\":[-3,14,0],\"size\":[6,6,0],\"uv\":[0,22]}]}]},{\"description\":{\"identifier\":\"creepie_armor\",\"texture_width\":32,\"texture_height\":32,\"visible_bounds_width\":2,\"visible_bounds_height\":2.5,\"visible_bounds_offset\":[0,0.75,0]},\"bones\":[{\"name\":\"root\",\"pivot\":[0,0,0]},{\"name\":\"creepie\",\"parent\":\"root\",\"pivot\":[0,0,0],\"rotation\":[0,0,0]},{\"name\":\"r_leg_front\",\"parent\":\"creepie\",\"pivot\":[-2,3,-2],\"cubes\":[{\"origin\":[-3,0,-3],\"size\":[2,3,2],\"inflate\":0.5,\"uv\":[16,12]}]},{\"name\":\"l_leg_front\",\"parent\":\"creepie\",\"pivot\":[2,3,-2],\"cubes\":[{\"origin\":[1,0,-3],\"size\":[2,3,2],\"inflate\":0.5,\"uv\":[16,12]}]},{\"name\":\"r_leg_back\",\"parent\":\"creepie\",\"pivot\":[-2,3,2],\"cubes\":[{\"origin\":[-3,0,1],\"size\":[2,3,2],\"inflate\":0.5,\"uv\":[16,17]}]},{\"name\":\"l_leg_back\",\"parent\":\"creepie\",\"pivot\":[2,3,2],\"cubes\":[{\"origin\":[1,0,1],\"size\":[2,3,2],\"inflate\":0.5,\"uv\":[16,17]}]},{\"name\":\"upper_body\",\"parent\":\"creepie\",\"pivot\":[0,3,0],\"cubes\":[{\"origin\":[-2,2,-2],\"size\":[4,6,4],\"inflate\":0.5,\"uv\":[0,12]}]},{\"name\":\"head\",\"parent\":\"upper_body\",\"pivot\":[0,8,0],\"cubes\":[{\"origin\":[-3,8,-3],\"size\":[6,6,6],\"inflate\":0.5,\"uv\":[0,0]}]}]}]}")[0];
        GeometryTree tree = GeometryTree.create(model);
        System.out.println(tree.getBones());
        System.out.println(tree.getRootBones().stream().map(this::getBoneTree).collect(Collectors.joining("\n")));

//        builder.add(AnimatedBone.create());
    }

    private String getBoneTree(AnimatedBone bone) {
        return this.getBoneTree("\t", bone);
    }

    private String getBoneTree(String prefix, AnimatedBone bone) {
        StringBuilder builder = new StringBuilder(bone.getBone().name()).append('\n');
        for (AnimatedBone child : bone.getChildren()) {
            builder.append(prefix).append(this.getBoneTree(prefix + '\t', child)).append('\n');
        }
        return builder.toString();
    }
}
