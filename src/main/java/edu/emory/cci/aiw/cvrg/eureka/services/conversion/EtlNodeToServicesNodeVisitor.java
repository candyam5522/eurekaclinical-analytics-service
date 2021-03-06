package edu.emory.cci.aiw.cvrg.eureka.services.conversion;

/*
 * #%L
 * Eureka Services
 * %%
 * Copyright (C) 2012 - 2014 Emory University
 * %%
 * This program is dual licensed under the Apache 2 and GPLv3 licenses.
 * 
 * Apache License, Version 2.0:
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * GNU General Public License version 3:
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import edu.emory.cci.aiw.cvrg.eureka.services.util.AbstractNodeVisitor;
import org.eurekaclinical.eureka.client.comm.BinaryOperator;
import org.eurekaclinical.eureka.client.comm.Literal;
import org.eurekaclinical.eureka.client.comm.Node;
import org.eurekaclinical.eureka.client.comm.UnaryOperator;

/**
 *
 * @author Andrew Post
 */
class EtlNodeToServicesNodeVisitor extends AbstractNodeVisitor {
	private Node node;
	private final CohortConversionSupport conversionSupport;

	public EtlNodeToServicesNodeVisitor() {
		this.conversionSupport = new CohortConversionSupport();
	}
	
	@Override
	public void visit(Literal literal) {
		Literal servicesLiteral = new Literal();
		servicesLiteral.setId(literal.getId());
		servicesLiteral.setName(
				this.conversionSupport.toPhenotypeKey(literal.getName()));
		servicesLiteral.setStart(literal.getStart());
		servicesLiteral.setFinish(literal.getFinish());
		this.node = servicesLiteral;
	}

	@Override
	public void visit(UnaryOperator unaryOperator) {
		UnaryOperator servicesUO = new UnaryOperator();
		servicesUO.setId(unaryOperator.getId());
		EtlNodeToServicesNodeVisitor v = new EtlNodeToServicesNodeVisitor();
		unaryOperator.getNode().accept(v);
		servicesUO.setNode(v.getNode());
		servicesUO.setOp(unaryOperator.getOp());
		this.node = servicesUO;
	}

	@Override
	public void visit(BinaryOperator binaryOperator) {
		BinaryOperator servicesBO = new BinaryOperator();
		servicesBO.setId(binaryOperator.getId());
		servicesBO.setOp(binaryOperator.getOp());
		EtlNodeToServicesNodeVisitor v = new EtlNodeToServicesNodeVisitor();
		binaryOperator.getLeftNode().accept(v);
		servicesBO.setLeftNode(v.getNode());
		binaryOperator.getRightNode().accept(v);
		servicesBO.setRightNode(v.getNode());
		this.node = servicesBO;
	}
	
	public Node getNode() {
		return this.node;
	}
	
}
