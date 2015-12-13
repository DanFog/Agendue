class CreateDevices < ActiveRecord::Migration
  def change
    create_table :devices do |t|
      t.string :os
      t.string :token

      t.timestamps
    end
  end
end
