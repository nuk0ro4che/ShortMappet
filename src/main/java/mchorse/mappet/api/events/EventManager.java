package mchorse.mappet.api.events;

import java.io.File;
import java.util.List;
import mchorse.mappet.CommonProxy;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.events.nodes.EventBaseNode;
import mchorse.mappet.api.utils.manager.BaseManager;
import mchorse.mappet.api.utils.nodes.NodeSystem;
import net.minecraft.class_2487;

public class EventManager extends BaseManager<NodeSystem<EventBaseNode>> {
   public EventManager(File folder) {
      super(folder);
   }

   protected NodeSystem<EventBaseNode> createData(String id, class_2487 tag) {
      NodeSystem<EventBaseNode> event = new NodeSystem<EventBaseNode>(CommonProxy.getEvents());
      if (tag != null) {
         event.deserializeNBT(tag);
      }

      return event;
   }

   public EventContext execute(String id, EventContext context) {
      NodeSystem<EventBaseNode> event = (NodeSystem)this.load(id);
      if (event != null) {
         this.execute(event, context);
      }

      return context;
   }

   public EventContext execute(NodeSystem<EventBaseNode> event, EventContext context) {
      if (event.main != null) {
         context.system = event;
         this.recursiveExecute(event, (EventBaseNode)event.main, context, false);
         context.submitDelayedExecutions();
      }

      return context;
   }

   public void recursiveExecute(NodeSystem<EventBaseNode> system, EventBaseNode node, EventContext context, boolean skipFirst) {
      if (context.executions < (Integer)Mappet.eventMaxExecutions.get()) {
         int result = skipFirst ? 0 : node.execute(context);
         if (result >= 0) {
            ++context.nesting;
            List<EventBaseNode> children = system.getChildren(node);
            if (result == 0) {
               for(EventBaseNode child : children) {
                  this.recursiveExecute(system, child, context, false);
               }
            } else if (result <= children.size()) {
               this.recursiveExecute(system, (EventBaseNode)children.get(result - 1), context, false);
            }

            --context.nesting;
         }

         ++context.executions;
      }
   }
}
