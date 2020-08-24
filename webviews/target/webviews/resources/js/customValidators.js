(function($) {
	$.fn.bootstrapValidator.validators.tipoDocumento = {
		html5Attributes : {
			message : 'message',
			field : 'field'
		},

		/**
		 * Check if input value equals to value of particular one
		 * 
		 * @param {BootstrapValidator}
		 *            validator The validator plugin instance
		 * @param {jQuery}
		 *            $field Field element
		 * @param {Object}
		 *            options Consists of the following key: - field: The name
		 *            of field that will be used to compare with current one
		 * @returns {Boolean}
		 */
		validate : function(validator, $field, options) {
			var tipoDocumento = $field.val();
			if (tipoDocumento == '') {
				return true;
			}

			var numeroDocumento = validator.getFieldElements(options.field);
			if (numeroDocumento == null) {
				return true;
			}

			if (validarIdentificacion(tipoDocumento, numeroDocumento.val())) {
				validator.updateStatus(options.field, validator.STATUS_VALID);
				return true;
			} else {
				validator.updateStatus(options.field, validator.STATUS_INVALID, 'tipoDocumento');
				return false;
			}
		}
	};
}(window.jQuery));
(function($) {
	$.fn.bootstrapValidator.validators.numeroDocumento = {
		html5Attributes : {
			message : 'message',
			field : 'field'
		},

		/**
		 * Check if input value equals to value of particular one
		 * 
		 * @param {BootstrapValidator}
		 *            validator The validator plugin instance
		 * @param {jQuery}
		 *            $field Field element
		 * @param {Object}
		 *            options Consists of the following key: - field: The name
		 *            of field that will be used to compare with current one
		 * @returns {Boolean}
		 */
		validate : function(validator, $field, options) {
			var numeroDocumento = $field.val();
			if (numeroDocumento == '') {
				return true;
			}

			var tipoDocumento = validator.getFieldElements(options.field);
			if (tipoDocumento == null) {
				return true;
			}

			if (validarIdentificacion(tipoDocumento.val(), numeroDocumento)) {
				validator.updateStatus(options.field, validator.STATUS_VALID);
				return true;
			} else {
				validator.updateStatus(options.field, validator.STATUS_INVALID, 'numeroDocumento');
				return false;
			}
		}
	};
}(window.jQuery));
