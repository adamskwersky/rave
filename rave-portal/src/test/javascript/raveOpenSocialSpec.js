/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
describe("Rave OpenSocial", function() {

    var container = null;

    beforeEach(function() {
        osapi = {
            container : {
                ServiceConfig : { API_PATH : 0 },
                ContainerConfig : {RENDER_DEBUG : 1},
                Container : function(args) {
                    var rpcHooks = {};

                    /** Mock usage machinery **/
                    this.args = function() {
                        return args;
                    };
                    this.rpcHooks = function() {
                        return rpcHooks;
                    };

                    /** OpenSocial Common Container functions **/
                    this.rpcRegister = function(string, func) {
                        rpcHooks[string] = func;
                    };
                    container = this;
                }
            }
        };
        gadgets = {
            util : {
                escapeString : function(string) {
                    return string;
                }
            }
        };

    });

    afterEach(function() {
        container = null;
        rave.opensocial.reset();
    });

    it("is OpenSocial", function() {
        expect(rave.opensocial.TYPE).toEqual("OpenSocial");
    });

    describe("Init", function() {

        it("initializes the OpenSocial container machinery", function() {
            rave.opensocial.init();

            expect(container.args()[0]).toEqual("/rpc");
            expect(container.args()[1]).toEqual("1");
        });

        it("Initializes the RPC Hooks Correctly", function() {
            rave.opensocial.init();
            hooks = container.rpcHooks();
            expect(hooks["resize_iframe"]).toBeDefined();
            expect(hooks["set_title"]).toBeDefined();
            expect(hooks["requestNavigateTo"]).toBeDefined();
        });

    });

    describe("Container", function() {
        it("initializes the container if not already done", function() {
            expect(container).toBeNull();
            var newContainer = rave.opensocial.container();
            expect(newContainer).toBe(container);
            expect(container).toBeDefined();
            expect(container.args()[0]).toEqual("/rpc");
        });

        it("returns the same reference if already initialized", function() {
            expect(container).toBeNull();
            var newContainer1 = rave.opensocial.container();
            var newContainer2 = rave.opensocial.container();
            expect(newContainer2).toBe(newContainer1);
        });
    });

    describe("RPC Hooks", function() {

        function getMockTitleArgs(id) {
            return {
                f : "frameId",
                a : "TITLE",
                gs : {
                    getActiveGadgetHolder : function() {
                        return {getElement : function() {
                            return { id : id }
                        }}
                    }
                }
            }
        }

        function getMockResizeArgs(size) {
            return {
                f: "frameId",
                a: size
            }
        }

        it("resizes Iframe if argument is less than height", function() {
            var mockElement = {style: {height: "-1px"}};
            spyOn(document, "getElementById").andReturn(mockElement);

            rave.opensocial.init();
            container.rpcHooks()["resize_iframe"](getMockResizeArgs(25));
            expect(mockElement.style.height).toEqual("25px");
        });

        it("resizes Iframe to max if height is greater than max", function() {
            var mockElement = {style: {height: "-1px"}};
            spyOn(document, "getElementById").andReturn(mockElement);

            rave.opensocial.init();
            container.rpcHooks()["resize_iframe"](getMockResizeArgs(2147483648));
            expect(mockElement.style.height).toEqual("2147483647px");
        });

        it("(resize) does not throw error if DOM element is null", function() {
            spyOn(document, "getElementById").andReturn(null);

            rave.opensocial.init();
            container.rpcHooks()["resize_iframe"](getMockResizeArgs(25));

            //If we reach this point there was no error
            expect(true).toBeTruthy();
        });

        it("set title changes the title DOM node", function() {
            var mockElement = {innerHTML : "NOTHING"};
            spyOn(document, "getElementById").andReturn(mockElement);

            rave.opensocial.init();
            container.rpcHooks()["set_title"](getMockTitleArgs("widget-7-body"));
            expect(mockElement.innerHTML).toEqual("TITLE");
            expect(document.getElementById).toHaveBeenCalledWith("widget-7-title");
        });

        it("set title does not throw error if DOM element is null", function() {
            spyOn(document, "getElementById").andReturn(null);

            rave.opensocial.init();
            container.rpcHooks()["set_title"](getMockTitleArgs("not-in-existence"));
            expect(true).toBeTruthy();
        });
    });
});