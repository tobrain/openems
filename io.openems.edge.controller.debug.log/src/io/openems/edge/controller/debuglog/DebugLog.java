package io.openems.edge.controller.debuglog;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openems.edge.common.component.AbstractOpenemsComponent;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.controller.api.Controller;

/**
 * This controller prints information about all available components on the
 * console.
 * 
 * @author stefan.feilmeier
 *
 */
@Designate(ocd = Config.class, factory = true)
@Component(name = "Controller.Debug.Log", immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
public class DebugLog extends AbstractOpenemsComponent implements Controller, OpenemsComponent {

	private final Logger log = LoggerFactory.getLogger(DebugLog.class);

	private List<OpenemsComponent> _components = new CopyOnWriteArrayList<>();

	@Reference(policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY, cardinality = ReferenceCardinality.MULTIPLE, target = "(!(service.factoryPid=Controller.Debug.Log))")
	void addComponent(OpenemsComponent component) {
		if (component.isEnabled()) {
			this._components.add(component);
		}
	}

	void removeComponent(OpenemsComponent component) {
		this._components.remove(component);
	}

	@Activate
	void activate(ComponentContext context, Config config) {
		super.activate(context, config.service_pid(), config.id(), config.enabled());
	}

	@Deactivate
	protected void deactivate() {
		super.deactivate();
	}

	@Override
	public void run() {
		StringBuilder b = new StringBuilder();
		/*
		 * Asks each component for its debugLog()-ChannelIds. Prints an aggregated log
		 * of those channelIds and their current values.
		 */
		this._components.stream() //
				.filter(c -> c.isEnabled()) // enabled components only
				.sorted((c1, c2) -> c1.id().compareTo(c2.id())) // sorted by Component-ID
				.forEachOrdered(component -> {
					String debugLog = component.debugLog();
					if (debugLog != null) {
						b.append(component.id());
						b.append("[" + debugLog + "] ");
					}
				});
		logInfo(this.log, b.toString());
	}
}
